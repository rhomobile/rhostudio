package rhogenwizard.launcher.rhodes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
import rhogenwizard.ConsoleHelper;
import rhogenwizard.LogFileHelper;
import rhogenwizard.OSHelper;
import rhogenwizard.PlatformType;
import rhogenwizard.ProcessListViewer;
import rhogenwizard.RunExeHelper;
import rhogenwizard.RunType;
import rhogenwizard.ShowMessageJob;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.constants.ConfigurationConstants;
import rhogenwizard.constants.DebugConstants;
import rhogenwizard.debugger.model.RhogenDebugTarget;
import rhogenwizard.sdk.facade.RhoTaskHolder;
import rhogenwizard.sdk.helper.TaskResultConverter;
import rhogenwizard.sdk.task.CleanPlatformTask;
import rhogenwizard.sdk.task.RunDebugRhodesAppTask;
import rhogenwizard.sdk.task.RunReleaseRhodesAppTask;

public class LaunchDelegate extends LaunchConfigurationDelegate implements IDebugEventSetListener 
{		
	private static final int sleepWaitChangeConsole = 1000;
	
	private static LogFileHelper rhodesLogHelper = new LogFileHelper();
	
	protected String            m_projectName = null;
	private String            m_runType     = null;
	private String            m_platformType = null;
	private boolean           m_isClean = false;
	private boolean           m_isReloadCode = false;
	private boolean           m_isTrace = false;
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
					ConsoleHelper.consoleBuildPrint("build started");
					
					if (mode.equals(ILaunchManager.DEBUG_MODE))
					{
						m_debugProcess = debugSelectedBuildConfiguration(project, type, launch);
							
						if (m_debugProcess == null)
						{
							ConsoleHelper.consoleBuildPrint("Error in build application");
							setProcessFinished(true);
							return;
						}
					}
					else
					{
					    Activator activator = Activator.getDefault();
					    activator.killProcessesForForRunReleaseRhodesAppTask();

					    ProcessListViewer rhosims = new ProcessListViewer(
					            "/RhoSimulator/rhosimulator.exe -approot=\'");

						if (runSelectedBuildConfiguration(project, type) != 0)
						{
							ConsoleHelper.consoleBuildPrint("Error in build application");
							setProcessFinished(true);
							return;
						}

						activator.storeProcessesForForRunReleaseRhodesAppTask(
						        rhosims.getNewProcesses());
					}
					
					startLogOutput(project, PlatformType.fromString(m_platformType), type);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
				ConsoleHelper.showAppConsole();
				setProcessFinished(true);
			}
		});
		cancelingThread.start();
	}

	private int runSelectedBuildConfiguration(IProject currProject, RunType selType) throws Exception
	{
		Map<String, Object> params = new HashMap<String, Object>();

		params.put(RunReleaseRhodesAppTask.workDir, currProject.getLocation().toOSString());
		params.put(RunReleaseRhodesAppTask.runType, selType);
		params.put(RunReleaseRhodesAppTask.platformType, PlatformType.fromString(m_platformType));
		params.put(RunReleaseRhodesAppTask.reloadCode, m_isReloadCode);
		params.put(RunReleaseRhodesAppTask.debugPort, new Integer(9000));
		params.put(RunReleaseRhodesAppTask.traceFlag, m_isTrace);
		
		Map results = RhoTaskHolder.getInstance().runTask(RunReleaseRhodesAppTask.class, params);
				
		return TaskResultConverter.getResultIntCode(results);
	}
	
	private IProcess debugSelectedBuildConfiguration(IProject currProject, RunType selType, ILaunch launch) throws Exception
	{
		Map<String, Object> params = new HashMap<String, Object>();

		params.put(RunDebugRhodesAppTask.workDir, currProject.getLocation().toOSString());
		params.put(RunDebugRhodesAppTask.platformType, PlatformType.fromString(m_platformType));
		params.put(RunDebugRhodesAppTask.reloadCode, m_isReloadCode);
		params.put(RunDebugRhodesAppTask.debugPort, new Integer(9000));
		params.put(RunDebugRhodesAppTask.launchObj, launch);
		params.put(RunDebugRhodesAppTask.traceFlag, m_isTrace);
		
		Map results = RhoTaskHolder.getInstance().runTask(RunDebugRhodesAppTask.class, params);
				
		return TaskResultConverter.getResultLaunchObj(results);
	}
	
	protected void setupConfigAttributes(ILaunchConfiguration configuration) throws CoreException
	{
		m_projectName  = configuration.getAttribute(ConfigurationConstants.projectNameCfgAttribute, "");
		m_platformType = configuration.getAttribute(ConfigurationConstants.platforrmCfgAttribute, "");
		m_isClean      = configuration.getAttribute(ConfigurationConstants.isCleanAttribute, false);
		m_runType      = configuration.getAttribute(ConfigurationConstants.simulatorType, "");
		m_isReloadCode = configuration.getAttribute(ConfigurationConstants.isReloadCodeAttribute, false);
		m_isTrace      = configuration.getAttribute(ConfigurationConstants.isTraceAttribute, false);
	}
	
	private void cleanSelectedPlatform(IProject project, boolean isClean) throws Exception
	{
		if (isClean) 
		{
			ConsoleHelper.consoleBuildPrint("Clean started");
			
			Map<String, Object> params = new HashMap<String, Object>();

			params.put(CleanPlatformTask.workDir, project.getLocation().toOSString());
			params.put(CleanPlatformTask.platformType, PlatformType.fromString(m_platformType));

			RhoTaskHolder.getInstance().runTask(CleanPlatformTask.class, params);
		}
	}

	@SuppressWarnings("deprecation")
	public synchronized void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, final IProgressMonitor monitor) throws CoreException 
	{
		setupConfigAttributes(configuration);
		
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(m_projectName);
		
		launchProject(configuration, mode, launch, monitor);
	}

	@SuppressWarnings("deprecation")
	public synchronized void launchProject(ILaunchConfiguration configuration, String mode, ILaunch launch, final IProgressMonitor monitor) throws CoreException 
	{
		try
		{
			RhogenDebugTarget target = null;
			setProcessFinished(false); 
			
			rhodesLogHelper.stopLog();
			
			setStandartConsoleOutputIsOff();
			
			ConsoleHelper.cleanBuildConsole();
			ConsoleHelper.showBuildConsole();
			
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
			
			final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(m_projectName);
			
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
				cleanSelectedPlatform(project, m_isClean);
			
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
				ConsoleHelper.consoleBuildPrint(e.getMessage());
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
			ShowMessageJob msgJob = new ShowMessageJob("", "Error", e.getMessage());
			msgJob.run(monitor);
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
	
	private void startLogOutput(IProject project, PlatformType type, RunType runType) throws Exception
	{
		rhodesLogHelper.startLog(type, project, runType);
	}
}

