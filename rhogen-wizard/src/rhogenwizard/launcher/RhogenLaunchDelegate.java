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

public class RhogenLaunchDelegate extends LaunchConfigurationDelegate implements IDebugEventSetListener 
{		
	private static final int sleepWaitChangeConsole = 1000;
	
	private static RhodesAdapter rhodesAdapter = new RhodesAdapter();
	private static LogFileHelper rhodesLogHelper = new LogFileHelper();
	
	private String            m_projectName = null;
	private String            m_platformName = null;
	private String			  m_appLogName = null; 
	private String            m_platformType = null;
	private boolean           m_isClean = false;
	private boolean           m_isReloadCode = false;
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
		final EPlatformType type = RhodesAdapter.convertPlatformFromDesc(m_platformName);
		
		Thread cancelingThread = new Thread(new Runnable() 
		{	
			@Override
			public void run() 
			{
				try 
				{
					ConsoleHelper.consolePrint("build started");
					
					if (mode.equals(ILaunchManager.DEBUG_MODE))
					{
						m_debugProcess = debugSelectedBuildConfiguration(project, type, launch);
							
						if (m_debugProcess == null)
						{
							ConsoleHelper.consolePrint("Error in build application");
							setProcessFinished(true);
							return;
						}
					}
					else
					{
						if (runSelectedBuildConfiguration(project, type) != 0)
						{
							ConsoleHelper.consolePrint("Error in build application");
							setProcessFinished(true);
							return;
						}
					}
					
					startLogOutput(project, type, new RunType(m_platformType));
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

	private int runSelectedBuildConfiguration(IProject currProject, EPlatformType selType) throws Exception
	{
		if (m_platformType.equals(RunType.platformDevice))
		{
			return rhodesAdapter.buildAppOnDevice(currProject.getLocation().toOSString(), selType);
		}
		else if (m_platformType.equals(RunType.platformSim))
		{
			return rhodesAdapter.buildAppOnSim(currProject.getLocation().toOSString(), selType);
		}
		else if (m_platformType.equals(RunType.platformRhoSim))
		{
			return rhodesAdapter.buildAppOnRhoSim(currProject.getLocation().toOSString(), selType, m_isReloadCode);
		}
		
		return 1;
	}
	
	private IProcess debugSelectedBuildConfiguration(IProject currProject, EPlatformType selType, ILaunch launch) throws Exception
	{
		IProcess  debugProcess = rhodesAdapter.debugApp(currProject.getName(), currProject.getLocation().toOSString(), selType, launch, m_isReloadCode);
		return debugProcess;
	}
	
	private void setupConfigAttributes(ILaunchConfiguration configuration) throws CoreException
	{
		m_projectName   = configuration.getAttribute(ConfigurationConstants.projectNameCfgAttribute, "");
		m_platformName  = configuration.getAttribute(ConfigurationConstants.platforrmCfgAttribute, "");
		m_appLogName    = configuration.getAttribute(ConfigurationConstants.prjectLogFileName, "");
		m_isClean       = configuration.getAttribute(ConfigurationConstants.isCleanAttribute, false);
		m_platformType  = configuration.getAttribute(ConfigurationConstants.simulatorType, "");
		m_isReloadCode  = configuration.getAttribute(ConfigurationConstants.isReloadCodeAttribute, false);
	}
	
	private void cleanSelectedPlatform(IProject project, boolean isClean) throws Exception
	{
		if (isClean) 
		{
			final EPlatformType type = RhodesAdapter.convertPlatformFromDesc(m_platformName);
			ConsoleHelper.consolePrint("Clean started");
			rhodesAdapter.cleanPlatform(project.getLocation().toOSString(), type);
		}
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
		ConsoleHelper.showBuildConsole();
		
		setupConfigAttributes(configuration);
		
		// stop blackberry simulator
		if (OSHelper.isWindows() && m_platformType.equals(RhodesAdapter.platformBlackBerry))
		{
			RunExeHelper.killBbSimulator();
		}

		if (m_projectName == null || m_projectName.length() == 0 || m_platformName == null || m_platformName.length() == 0) 
		{
			throw new IllegalArgumentException("Error - Platform and project name should be assigned");
		}
		
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(m_projectName);
		
		if (!project.isOpen()) 
		{
			throw new IllegalArgumentException("Error - Project not found");
		}
		
		if (mode.equals(ILaunchManager.DEBUG_MODE))
		{
			ShowPerspectiveJob job = new ShowPerspectiveJob("show debug perspective", DebugConstants.debugPerspectiveId);
			job.run(monitor);
			
			try 
			{
				OSHelper.killProcess("rhosimulator");
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			target = new RhogenDebugTarget(launch, null, RhogenDebugTarget.EDebugPlatfrom.eRhodes);
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
	public void handleDebugEvents(DebugEvent[] events) 
	{
	}
	
	private void startLogOutput(IProject project, EPlatformType type, RunType runType) throws Exception
	{
		rhodesLogHelper.startLog(type, project, runType);
	}
}

