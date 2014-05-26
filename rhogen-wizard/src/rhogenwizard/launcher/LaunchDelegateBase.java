package rhogenwizard.launcher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.preferences.IDebugPreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import rhogenwizard.Activator;
import rhogenwizard.BuildType;
import rhogenwizard.ConsoleHelper;
import rhogenwizard.DialogUtils;
import rhogenwizard.LogFileHelper;
import rhogenwizard.OSHelper;
import rhogenwizard.PlatformType;
import rhogenwizard.ProcessListViewer;
import rhogenwizard.RunType;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.constants.ConfigurationConstants;
import rhogenwizard.constants.DebugConstants;
import rhogenwizard.debugger.model.DebugTarget;
import rhogenwizard.rhohub.TokenChecker;
import rhogenwizard.sdk.task.CleanPlatformTask;
import rhogenwizard.sdk.task.IDebugTask;
import rhogenwizard.sdk.task.RunTask;
import rhogenwizard.sdk.task.run.RhohubDebugRhodesAppTask;
import rhogenwizard.sdk.task.run.RhohubRunRhodesAppTask;
import rhogenwizard.sdk.task.run.LocalDebugRhodesAppTask;
import rhogenwizard.sdk.task.run.LocalRunRhodesAppTask;

public class LaunchDelegateBase extends LaunchConfigurationDelegate implements IDebugEventSetListener 
{		
	private static class FailBuildExtension extends Throwable
	{
		private static final long serialVersionUID = 5907642700379669820L;

		private final String m_runCommand; 
		
		public FailBuildExtension(String runCommand) 
		{			
			m_runCommand = runCommand;
		}		
		
		public String getCommand()
		{
			return m_runCommand;
		}
	}
	
	private static LogFileHelper rhodesLogHelper = new LogFileHelper();
	
	protected String          m_projectName   = null;
	private String            m_runType       = null;
	private String            m_buildType     = null;    
	private String            m_platformType  = null;
	private boolean           m_isClean       = false;
	private boolean           m_isReloadCode  = false;
	private boolean           m_isTrace       = false;
	private AtomicBoolean     m_buildFinished = new AtomicBoolean();
	private IProcess          m_debugProcess  = null;
	private final String      m_startPathOverride;
	private final String[]    m_additionalRubyExtensions;
	
	public LaunchDelegateBase(String startPathOverride, String[] additionalRubyExtensions)
	{
	    m_startPathOverride        = startPathOverride;
	    m_additionalRubyExtensions = additionalRubyExtensions;
	}
		
	private void setProcessFinished(boolean b)
	{
		m_buildFinished.set(b);
	}

	private boolean getProcessFinished()
	{
		return m_buildFinished.get();
	}

	private void releaseBuild(IProject project, RunType type) throws Exception, FailBuildExtension
	{
        Activator activator = Activator.getDefault();
        activator.killProcessesForForRunReleaseRhodesAppTask();

        ProcessListViewer rhosims = new ProcessListViewer("/RhoSimulator/rhosimulator.exe \"-approot=\'");

        if (!runSelectedBuildConfiguration(project, type))
        {
            throw new FailBuildExtension("");
        }

        activator.storeProcessesForForRunReleaseRhodesAppTask(rhosims.getNewProcesses());
	}
	
	private IProcess debugBuild(IProject project, RunType type, ILaunch launch) throws Exception, FailBuildExtension
	{
        m_debugProcess = debugSelectedBuildConfiguration(project, type, launch);
        
        if (m_debugProcess == null)
        {
            throw new FailBuildExtension("");
        }

        return m_debugProcess;        
	}
	
	public void startBuildThread(final IProject project, final String mode, final ILaunch launch)
	{
		final RunType type = RunType.fromString(m_runType);
		
		Thread cancelingThread = new Thread(new Runnable() 
		{	
			@Override
			public void run() 
			{
				try 
				{
					ConsoleHelper.getBuildConsole().show();
					
				    ConsoleHelper.Stream stream = ConsoleHelper.getBuildConsole().getStream();
					stream.println("build started");
					
					if (mode.equals(ILaunchManager.DEBUG_MODE))
					{
					    debugBuild(project, type, launch);
					}
					else
					{
					    releaseBuild(project, type);
					}
					
					rhodesLogHelper.startLog(PlatformType.fromId(m_platformType), project, type);		
					
					ConsoleHelper.getAppConsole().show();
				} 
				catch (FailBuildExtension e) 
				{
					ConsoleHelper.Stream stream = ConsoleHelper.getBuildConsole().getStream();
					stream.println("Error in build application. Build is terminated.");
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
				setProcessFinished(true);
			}
		});
		cancelingThread.start();
	}

	private boolean runSelectedBuildConfiguration(IProject currProject, RunType selType) throws Exception
	{
		if (!TokenChecker.processToken(currProject.getLocation().toOSString()))
			return false;
		
        RunTask task;
		if (BuildType.fromId(m_buildType) == BuildType.eRhoHub) {
            task = new RhohubRunRhodesAppTask(currProject.getLocation().toOSString(),
                PlatformType.fromId(m_platformType), m_isTrace, m_startPathOverride,
                m_additionalRubyExtensions);
		} else {
            task = new LocalRunRhodesAppTask(currProject.getLocation().toOSString(),
                PlatformType.fromId(m_platformType), selType, m_isReloadCode, m_isTrace,
                m_startPathOverride, m_additionalRubyExtensions);
		}
				
		task.run();

		return task.isOk();
	}
	
	private IProcess debugSelectedBuildConfiguration(IProject currProject, RunType selType, ILaunch launch) throws Exception
	{
		if (!TokenChecker.processToken(currProject.getLocation().toOSString()))
			return null;
		IDebugTask task;
        if (BuildType.fromId(m_buildType) == BuildType.eRhoHub)
        {
            task = new RhohubDebugRhodesAppTask(launch, selType,
                currProject.getLocation().toOSString(), currProject.getName(),
                PlatformType.fromId(m_platformType), m_isReloadCode, m_startPathOverride,
                m_additionalRubyExtensions);
        }
        else
        {
            task = new LocalDebugRhodesAppTask(launch, selType,
                currProject.getLocation().toOSString(), currProject.getName(),
                PlatformType.fromId(m_platformType), m_isReloadCode, m_isTrace, m_startPathOverride,
                m_additionalRubyExtensions);
        }
		task.run();
		
		return task.getDebugProcess();
	}
	
	protected void setupConfigAttributes(ILaunchConfiguration configuration) throws CoreException
	{
		m_projectName   = configuration.getAttribute(ConfigurationConstants.projectNameCfgAttribute, "");
		m_platformType  = configuration.getAttribute(ConfigurationConstants.platformCfgAttribute, "");
		m_buildType     = configuration.getAttribute(ConfigurationConstants.buildCfgAttribute, "");
		m_isClean       = configuration.getAttribute(ConfigurationConstants.isCleanAttribute, false);
		m_runType       = configuration.getAttribute(ConfigurationConstants.simulatorType, "");
		m_isReloadCode  = configuration.getAttribute(ConfigurationConstants.isReloadCodeAttribute, false);
		m_isTrace       = configuration.getAttribute(ConfigurationConstants.isTraceAttribute, false);
	}
	
	private void cleanSelectedPlatform(IProject project, boolean isClean, IProgressMonitor monitor) throws FileNotFoundException
	{
		if (isClean) 
		{			
			RunTask task = new CleanPlatformTask(project.getLocation().toOSString(),
			    PlatformType.fromId(m_platformType));
			task.run(monitor);		
		}
	}

	public synchronized void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, final IProgressMonitor monitor) throws CoreException 
	{
		setupConfigAttributes(configuration);
		
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(m_projectName);
				
		launchLocalProject(project, configuration, mode, launch, monitor);
	}

	public synchronized void launchLocalProject(IProject project, ILaunchConfiguration configuration, String mode, ILaunch launch, final IProgressMonitor monitor) throws CoreException 
	{
		try
		{
			DebugTarget target = null;
			setProcessFinished(false); 
			
			rhodesLogHelper.stopLog();
			
			setStandartConsoleOutputIsOff();
			
            ConsoleHelper.getBuildConsole().clear();
            ConsoleHelper.getBuildConsole().show();
			
			setupConfigAttributes(configuration);
			
			if (m_projectName == null || m_projectName.length() == 0 || m_runType == null || m_runType.length() == 0) 
			{
				throw new IllegalArgumentException("Platform and project name should be assigned");
			}
						
			if (!project.isOpen()) 
			{
				throw new IllegalArgumentException("Project " + project.getName() + " not found");
			}		
			
			if (mode.equals(ILaunchManager.DEBUG_MODE))
			{
				ShowPerspectiveJob job = new ShowPerspectiveJob("show debug perspective", DebugConstants.debugPerspectiveId);
				job.schedule();
				
				try 
				{
					OSHelper.killProcess("rhosimulator");
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
				target = new DebugTarget(launch, null, project, RunType.fromString(m_runType), PlatformType.fromId(m_platformType));
			}
			
			try
			{
				cleanSelectedPlatform(project, m_isClean, monitor);
			
				startBuildThread(project, mode, launch);
	
				while(true)
				{
					if (monitor.isCanceled()) 
				    {
						OSHelper.killProcess("ruby");
						return;
				    }
					
					if (getProcessFinished())
					{
						break;
					}

					Thread.sleep(100);
				}
			}
		    catch (InterruptedException e) 
		    {
		    	e.printStackTrace();
		    	Activator.logError(e);
		    }
			catch (FileNotFoundException e) 
			{
				DialogUtils.error("Missing build.yml", "Configuration file build.yml is not found in application folder. Build was terminated.");
				Activator.logError(e);
			}
			catch (IOException e) 
		    {
				e.printStackTrace();
				Activator.logError(e);
			}
			
			monitor.done();
			
			if (mode.equals(ILaunchManager.DEBUG_MODE))
			{
				target.setProcess(m_debugProcess);
				launch.addDebugTarget(target);			
			}
		}
		catch (IllegalArgumentException e) 
		{
			DialogUtils.error("Error", e.getMessage());
		}
	}
	
	void setStandartConsoleOutputIsOff()
	{
		IPreferenceStore prefs = DebugUIPlugin.getDefault().getPreferenceStore();
		
		prefs.setDefault(IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT, false);
		prefs.setDefault(IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR, false);
		prefs.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT, false);
		prefs.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR, false);
	}

    @Override
    public void handleDebugEvents(DebugEvent[] events)
    {
    }
}

