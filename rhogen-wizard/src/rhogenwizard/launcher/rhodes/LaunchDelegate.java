package rhogenwizard.launcher.rhodes;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import rhogenwizard.Activator;
import rhogenwizard.ConsoleHelper;
import rhogenwizard.DialogUtils;
import rhogenwizard.LogFileHelper;
import rhogenwizard.OSHelper;
import rhogenwizard.PlatformType;
import rhogenwizard.ProcessListViewer;
import rhogenwizard.RunExeHelper;
import rhogenwizard.RunType;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.constants.ConfigurationConstants;
import rhogenwizard.constants.DebugConstants;
import rhogenwizard.debugger.model.RhogenDebugTarget;
import rhogenwizard.rhohub.RhoHub;
import rhogenwizard.sdk.task.CleanPlatformTask;
import rhogenwizard.sdk.task.RunTask;
import rhogenwizard.sdk.task.run.RunDebugRhodesAppTask;
import rhogenwizard.sdk.task.run.RunReleaseRhodesAppTask;

public class LaunchDelegate extends LaunchConfigurationDelegate implements IDebugEventSetListener 
{		
	private static LogFileHelper rhodesLogHelper = new LogFileHelper();
	
	protected String          m_projectName = null;
	private String            m_runType     = null;
	private String            m_platformType = null;
	private boolean           m_isClean = false;
	private boolean           m_isReloadCode = false;
	private boolean           m_isTrace = false;
	private boolean           m_isRhohubBuild = false;
	private AtomicBoolean     m_buildFinished = new AtomicBoolean();
	private IProcess          m_debugProcess = null;
		
	private void setProcessFinished(boolean b)
	{
		m_buildFinished.set(b);
	}

	private boolean getProcessFinished()
	{
		return m_buildFinished.get();
	}

	private void releaseBuild(IProject project, RunType type) throws Exception
	{
	    ConsoleHelper.Stream stream = ConsoleHelper.getBuildConsole().getStream();
	    
        Activator activator = Activator.getDefault();
        activator.killProcessesForForRunReleaseRhodesAppTask();

        ProcessListViewer rhosims = new ProcessListViewer("/RhoSimulator/rhosimulator.exe -approot=\'");

        if (!runSelectedBuildConfiguration(project, type))
        {
            stream.println("Error in build application");
            setProcessFinished(true);
            return;
        }

        activator.storeProcessesForForRunReleaseRhodesAppTask(rhosims.getNewProcesses());
	}
	
	private IProcess debugBuild(IProject project, RunType type, ILaunch launch) throws Exception
	{
	    ConsoleHelper.Stream stream = ConsoleHelper.getBuildConsole().getStream();
	    
        m_debugProcess = debugSelectedBuildConfiguration(project, type, launch);
        
        if (m_debugProcess == null)
        {
            stream.println("Error in build application");
            setProcessFinished(true);
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
					
					rhodesLogHelper.startLog(PlatformType.fromString(m_platformType), project, type);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
                ConsoleHelper.getAppConsole().show();
				setProcessFinished(true);
			}
		});
		cancelingThread.start();
	}

	private boolean runSelectedBuildConfiguration(IProject currProject, RunType selType) throws Exception
	{
		RunTask task = new RunReleaseRhodesAppTask(currProject.getLocation().toOSString(),
		    PlatformType.fromString(m_platformType), selType, m_isReloadCode, m_isTrace);
		task.run();
		return task.isOk();
	}
	
	private IProcess debugSelectedBuildConfiguration(IProject currProject, RunType selType, ILaunch launch) throws Exception
	{
		RunDebugRhodesAppTask task = new RunDebugRhodesAppTask(launch, currProject.getLocation().toOSString(),
		    currProject.getName(), PlatformType.fromString(m_platformType), m_isReloadCode, m_isTrace);
		task.run();
		return task.getDebugProcess();
	}
	
	protected void setupConfigAttributes(ILaunchConfiguration configuration) throws CoreException
	{
		m_projectName   = configuration.getAttribute(ConfigurationConstants.projectNameCfgAttribute, "");
		m_platformType  = configuration.getAttribute(ConfigurationConstants.platforrmCfgAttribute, "");
		m_isClean       = configuration.getAttribute(ConfigurationConstants.isCleanAttribute, false);
		m_runType       = configuration.getAttribute(ConfigurationConstants.simulatorType, "");
		m_isReloadCode  = configuration.getAttribute(ConfigurationConstants.isReloadCodeAttribute, false);
		m_isTrace       = configuration.getAttribute(ConfigurationConstants.isTraceAttribute, false);		
		m_isRhohubBuild = configuration.getAttribute(ConfigurationConstants.isUseRhoHub, false);
	}
	
	private void cleanSelectedPlatform(IProject project, boolean isClean, IProgressMonitor monitor)
	{
		if (isClean) 
		{
			RunTask task = new CleanPlatformTask(project.getLocation().toOSString(), PlatformType.fromString(m_platformType));
			task.run(monitor);
		}
	}

	@SuppressWarnings("deprecation")
	public synchronized void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, final IProgressMonitor monitor) throws CoreException 
	{
		setupConfigAttributes(configuration);
		
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(m_projectName);
		
//		if (m_isRhohubBuild)
//		{
//		    launchRemoteProject(project, configuration, mode, launch, monitor);
//		    return; //TODO its temp statement
//		}
		
		launchLocalProject(project, configuration, mode, launch, monitor);
	}

    @SuppressWarnings("deprecation")
    public synchronized void launchRemoteProject(IProject project, ILaunchConfiguration configuration, String mode, ILaunch launch, final IProgressMonitor monitor) throws CoreException
    {
         //RhoHub.getInstance(configuration).findRemoteApp(project);
    }

	@SuppressWarnings("deprecation")
	public synchronized void launchLocalProject(IProject project, ILaunchConfiguration configuration, String mode, ILaunch launch, final IProgressMonitor monitor) throws CoreException 
	{
		try
		{
			RhogenDebugTarget target = null;
			setProcessFinished(false); 
			
			rhodesLogHelper.stopLog();
			
			setStandartConsoleOutputIsOff();
			
            ConsoleHelper.getBuildConsole().clear();
            ConsoleHelper.getBuildConsole().show();
			
			setupConfigAttributes(configuration);
			
			PlatformType currPlType = PlatformType.fromString(m_platformType);
			
			// stop blackberry simulator
			if (OSHelper.isWindows() && currPlType == PlatformType.eBb)
			{
				RunExeHelper.killBbSimulator();
			}
	
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
				
				target = new RhogenDebugTarget(launch, null, project);
			}
			
			try
			{
				cleanSelectedPlatform(project, m_isClean, monitor);
			
				startBuildThread(project, mode, launch);
	
				while(true)
				{
					try 
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
				    catch (InterruptedException e) 
				    {
				    	e.printStackTrace();
				    }
				}
			}
			catch(IllegalArgumentException e)
			{
			    Activator.logError(e);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
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

