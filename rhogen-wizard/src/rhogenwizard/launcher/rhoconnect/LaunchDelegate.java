package rhogenwizard.launcher.rhoconnect;

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
import rhogenwizard.DialogUtils;
import rhogenwizard.LogFileHelper;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.constants.ConfigurationConstants;
import rhogenwizard.constants.DebugConstants;
import rhogenwizard.debugger.model.RhogenDebugTarget;
import rhogenwizard.sdk.task.RunTask;
import rhogenwizard.sdk.task.StopSyncAppTask;
import rhogenwizard.sdk.task.run.RunDebugRhoconnectAppTask;
import rhogenwizard.sdk.task.run.RunReleaseRhoconnectAppTask;

@SuppressWarnings("restriction")
public class LaunchDelegate extends LaunchConfigurationDelegate implements IDebugEventSetListener 
{		
	private static LogFileHelper rhodesLogHelper = new LogFileHelper();
	
	private String        m_projectName = null;
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
					ConsoleHelper.getBuildConsole().getStream().println("build started");
					
					if (mode.equals(ILaunchManager.DEBUG_MODE))
					{
						m_debugProcess = debugSelectedBuildConfiguration(project, launch);
							
						if (m_debugProcess == null)
						{
						    ConsoleHelper.getBuildConsole().getStream().println("Error in build application");
							setProcessFinished(true);
							return;
						}
					}
					else
					{
						runSelectedBuildConfiguration(project);
					}
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

	private void runSelectedBuildConfiguration(IProject currProject) throws Exception
	{
		RunTask task = new RunReleaseRhoconnectAppTask(currProject.getLocation().toOSString());
		task.run();
	}
	
	private IProcess debugSelectedBuildConfiguration(IProject currProject, ILaunch launch) throws Exception
	{
		RunDebugRhoconnectAppTask task = new RunDebugRhoconnectAppTask(currProject.getLocation().toOSString(),
		    currProject.getName(), launch);
		task.run();
		return task.getDebugProcess();
	}
	
	private void setupConfigAttributes(ILaunchConfiguration configuration) throws CoreException
	{
		m_projectName   = configuration.getAttribute(ConfigurationConstants.projectNameCfgAttribute, "");
	}
				
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public synchronized void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, final IProgressMonitor monitor) throws CoreException 
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
						    new StopSyncAppTask().makeJob("StopSyncAppTask").schedule();
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

