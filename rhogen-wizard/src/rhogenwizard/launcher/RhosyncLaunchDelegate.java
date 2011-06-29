package rhogenwizard.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.dltk.console.IScriptConsoleIO;
import org.eclipse.dltk.console.IScriptExecResult;
import org.eclipse.dltk.console.IScriptInterpreter;
import org.eclipse.dltk.console.ScriptExecResult;
import org.eclipse.dltk.console.ScriptInterpreterManager;
import org.eclipse.dltk.console.ui.IScriptConsole;
import org.eclipse.dltk.console.ui.ScriptConsoleManager;
import org.eclipse.dltk.debug.ui.DebugConsoleManager;
import org.eclipse.dltk.debug.ui.ScriptDebugConsole;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.LogFileHelper;
import rhogenwizard.OSHelper;
import rhogenwizard.RhodesAdapter;
import rhogenwizard.RhodesAdapter.EPlatformType;
import rhogenwizard.RunExeHelper;
import rhogenwizard.RunType;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.builder.RhogenBuilder;
import rhogenwizard.constants.ConfigurationConstants;
import rhogenwizard.constants.DebugConstants;
import rhogenwizard.debugger.model.RhogenDebugTarget;

import org.eclipse.dltk.debug.ui.display.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.part.IPageBookViewPage;

public class RhosyncLaunchDelegate extends LaunchConfigurationDelegate implements IDebugEventSetListener 
{		
	private static RhodesAdapter rhodesAdapter = new RhodesAdapter();
	private static LogFileHelper rhodesLogHelper = new LogFileHelper();
	
	private String            m_projectName = null;
	private String            m_platformName = null;
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
		Thread cancelingThread = new Thread(new Runnable() 
		{	
			@Override
			public void run() 
			{
				try 
				{
//					OSHelper.setEnvVariable("RHOHOST", "127.0.0.1");
//					OSHelper.setEnvVariable("RHOPORT", "9000");
//					OSHelper.setEnvVariable("RHOAPP", project.getLocation().toOSString());
					
					ConsoleHelper.consolePrint("build started");
					
					if (mode.equals(ILaunchManager.DEBUG_MODE))
					{
						m_debugProcess = debugSelectedBuildConfiguration(project, launch);
							
						if (m_debugProcess == null)
						{
							ConsoleHelper.consolePrint("Error in build application");
							setProcessFinished(true);
							return;
						}
					}
					else
					{
						if (runSelectedBuildConfiguration(project) != 0)
						{
							ConsoleHelper.consolePrint("Error in build application");
							setProcessFinished(true);
							return;
						}
					}
					
					startLogOutput(project, EPlatformType.eRsync);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
				//ConsoleHelper.showAppConsole();
				setProcessFinished(true);
			}
		});
		cancelingThread.start();
	}

	private int runSelectedBuildConfiguration(IProject currProject) throws Exception
	{
		return rhodesAdapter.buildRhosyncApp(currProject.getLocation().toOSString());
	}
	
	private IProcess debugSelectedBuildConfiguration(IProject currProject, ILaunch launch) throws Exception
	{
		IProcess  debugProcess = rhodesAdapter.debugSyncApp(currProject.getName(), currProject.getLocation().toOSString(), launch);
		return debugProcess;
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
		RhogenDebugTarget target = null;
		setProcessFinished(false); 
		
		rhodesLogHelper.stopLog();
		
		ConsoleHelper.cleanBuildConsole();
		
		setupConfigAttributes(configuration);

		if (m_projectName == null || m_projectName.length() == 0) 
		{
			throw new IllegalArgumentException("Error - Platform and project name should be assigned");
		}
		
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(m_projectName);
		
		if (!project.isOpen()) 
		{
			throw new IllegalArgumentException("Error - Project not found");
		}

		try
		{
			if (mode.equals(ILaunchManager.DEBUG_MODE))
			{
				ShowPerspectiveJob job = new ShowPerspectiveJob("show debug perspective", DebugConstants.debugPerspectiveId);
				job.run(monitor);
				
				OSHelper.killProcess("ruby");
				
				target = new RhogenDebugTarget(launch, null, RhogenDebugTarget.EDebugPlatfrom.eRhosync);
			}
		
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
			ConsoleHelper.consolePrint(e.getMessage());
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

	@Override
	protected IProject[] getBuildOrder(ILaunchConfiguration configuration, String mode) throws CoreException 
	{
		if (m_projectName != null) 
		{
			m_projectName = m_projectName.trim();
			
			if (m_projectName.length() > 0) 
			{
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(m_projectName);

				project.setSessionProperty(RhogenBuilder.getPlatformQualifier(), m_platformName);
				
				IProject[] findProjects = { project };
				
				return findProjects;
			}
		}

		return null;
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) 
	{
	}
	
	private void startLogOutput(IProject project, EPlatformType type) throws Exception
	{
		RunType runType = new RunType(RunType.ERunType.eEmulator);
		rhodesLogHelper.startLog(type, project, runType);
	}
}

