package rhogenwizard.launcher.rhoconnect;

import java.util.HashMap;
import java.util.Map;
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

import rhogenwizard.ConsoleHelper;
import rhogenwizard.DialogUtils;
import rhogenwizard.LogFileHelper;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.constants.ConfigurationConstants;
import rhogenwizard.constants.DebugConstants;
import rhogenwizard.debugger.model.RhogenDebugTarget;
import rhogenwizard.sdk.facade.RhoTaskHolder;
import rhogenwizard.sdk.helper.TaskResultConverter;
import rhogenwizard.sdk.task.RunDebugRhoconnectAppTask;
import rhogenwizard.sdk.task.RunReleaseRhoconnectAppTask;
import rhogenwizard.sdk.task.StopRhoconnectAppAdapter;

public class LaunchDelegate extends LaunchConfigurationDelegate implements IDebugEventSetListener 
{		
	private static LogFileHelper rhodesLogHelper = new LogFileHelper();
	
	private String        m_projectName = null;
	private String        m_platformName = null;
	private AtomicBoolean m_buildFinished = new AtomicBoolean();
	private IProcess      m_debugProcess = null;
		
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
						m_debugProcess = debugSelectedBuildConfiguration(project, launch);
							
						if (m_debugProcess == null)
						{
							ConsoleHelper.consoleBuildPrint("Error in build application");
							setProcessFinished(true);
							return;
						}
					}
					else
					{
						if (runSelectedBuildConfiguration(project) != 0)
						{
							ConsoleHelper.consoleBuildPrint("Error in build application");
							setProcessFinished(true);
							return;
						}
					}
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

	private int runSelectedBuildConfiguration(IProject currProject) throws Exception
	{
		Map<String, Object> params = new HashMap<String, Object>();

		params.put(RunReleaseRhoconnectAppTask.workDir, currProject.getLocation().toOSString());
		
		Map results = RhoTaskHolder.getInstance().runTask(RunReleaseRhoconnectAppTask.class, params);
				
		return TaskResultConverter.getResultIntCode(results);		
	}
	
	private IProcess debugSelectedBuildConfiguration(IProject currProject, ILaunch launch) throws Exception
	{
		Map<String, Object> params = new HashMap<String, Object>();

		params.put(RunDebugRhoconnectAppTask.workDir, currProject.getLocation().toOSString());
		params.put(RunDebugRhoconnectAppTask.appName, currProject.getName());
		params.put(RunDebugRhoconnectAppTask.launchObj, launch);
		
		Map results = RhoTaskHolder.getInstance().runTask(RunDebugRhoconnectAppTask.class, params);
				
		return TaskResultConverter.getResultLaunchObj(results);
	}
	
	private void setupConfigAttributes(ILaunchConfiguration configuration) throws CoreException
	{
		m_projectName   = configuration.getAttribute(ConfigurationConstants.projectNameCfgAttribute, "");
	}
				
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@SuppressWarnings("deprecation")
	public synchronized void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, final IProgressMonitor monitor) throws CoreException 
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
	
			if (m_projectName == null || m_projectName.length() == 0) 
			{
				throw new IllegalArgumentException("Project should be assigned");
			}
			
			final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(m_projectName);
			
			if (!project.isOpen()) 
			{
				throw new IllegalArgumentException("Project " + project.getName() + " not found");
			}
	
			try
			{		
				if (mode.equals(ILaunchManager.DEBUG_MODE))
				{
					ShowPerspectiveJob job = new ShowPerspectiveJob("show debug perspective", DebugConstants.debugPerspectiveId);
					job.schedule();
					
					target = new RhogenDebugTarget(launch, null, project/*RhogenDebugTarget.EDebugPlatfrom.eRhosync*/);
				}
			
				startBuildThread(project, mode, launch);
	
				while(true)
				{
					try 
				    {
						if (monitor.isCanceled()) 
					    {
							StopRhoconnectAppAdapter.stopRhoconnectApp();
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
			DialogUtils.error("Error", e.getMessage());
		}
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) 
	{
	}

	void setStandartConsoleOutputIsOff()
	{
		IPreferenceStore prefs = DebugUIPlugin.getDefault().getPreferenceStore();
		
		prefs.setDefault(IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT, false);
		prefs.setDefault(IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR, false);
		prefs.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT, false);
		prefs.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR, false);
	}
}

