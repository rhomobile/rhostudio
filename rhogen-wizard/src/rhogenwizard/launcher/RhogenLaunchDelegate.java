package rhogenwizard.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.progress.UIJob;

import rhogenwizard.AsyncStreamReader;
import rhogenwizard.ConsoleHelper;
import rhogenwizard.LogFileHelper;
import rhogenwizard.OSHelper;
import rhogenwizard.RhodesAdapter;
import rhogenwizard.RhodesAdapter.EPlatformType;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.builder.RhogenBuilder;
import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.debugger.RhogenConstants;
import rhogenwizard.debugger.model.RhogenDebugTarget;

public class RhogenLaunchDelegate extends LaunchConfigurationDelegate implements IDebugEventSetListener 
{		
	public static final String projectNameCfgAttribute = "project_name";
	public static final String platforrmCfgAttribute = "platform";
	public static final String platforrmDeviceCfgAttribute = "device";
	public static final String androidVersionAttribute = "aversion";
	public static final String androidEmuNameAttribute = "aemuname";
	public static final String blackberryVersionAttribute = "bversion";
	public static final String iphoneVersionAttribute = "ipversion";
	public static final String isCleanAttribute = "clean";
	public static final String prjectLogFileName = "log_filename";
	
	private static RhodesAdapter rhodesAdapter = new RhodesAdapter();
	private static LogFileHelper rhodesLogHelper = new LogFileHelper();
	
	private String            m_projectName = null;
	private String            m_platformName = null;
	private String			  m_appLogName = null; 
	private boolean           m_isClean = false;
	private boolean           m_onDevice = false;
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
						if (type != RhodesAdapter.EPlatformType.eEmu)
						{
							if (rhodesAdapter.buildApp(project.getLocation().toOSString(), type, m_onDevice) != 0)
							{
								ConsoleHelper.consolePrint("Error in build application");
								setProcessFinished(true);
								return;
							}
						}
						else
						{
							m_debugProcess = rhodesAdapter.debugApp(m_projectName, project.getLocation().toOSString(), type, launch, m_onDevice);
							
							if (m_debugProcess == null)
							{
								ConsoleHelper.consolePrint("Error in build application");
								setProcessFinished(true);
								return;
							}
						}
					}
					else
					{
						if (rhodesAdapter.buildApp(project.getLocation().toOSString(), type, m_onDevice) != 0)
						{
							ConsoleHelper.consolePrint("Error in build application");
							setProcessFinished(true);
							return;
						}
					}
					
					startLogOutput(project, type);
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
	
	private void setupConfigAttributes(ILaunchConfiguration configuration) throws CoreException
	{
		m_projectName   = configuration.getAttribute(projectNameCfgAttribute, "");
		m_platformName  = configuration.getAttribute(platforrmCfgAttribute, "");
		m_appLogName    = configuration.getAttribute(prjectLogFileName, "");
		m_isClean       = configuration.getAttribute(isCleanAttribute, false);
		m_onDevice      = configuration.getAttribute(platforrmDeviceCfgAttribute, false);		
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
		
		setupConfigAttributes(configuration);

		if (m_projectName == null || m_projectName.length() == 0 || m_platformName == null || m_platformName.length() == 0) 
		{
			throw new IllegalArgumentException("Error - Platform and project name should be assigned");
		}
		
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(m_projectName);
		
		if (!project.isOpen()) 
		{
			throw new IllegalArgumentException("Error - Project not found ");
		}
		
		if (mode.equals(ILaunchManager.DEBUG_MODE))
		{
			ShowPerspectiveJob job = new ShowPerspectiveJob("show debug perspective", RhogenConstants.debugPerspectiveId);
			job.run(monitor);
			
			try {
				OSHelper.killProcess("rhosimulator");
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			
			target = new RhogenDebugTarget(launch, null);
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
		
		final EPlatformType plType = RhodesAdapter.convertPlatformFromDesc(m_platformName);
		
		if (mode.equals(ILaunchManager.DEBUG_MODE) && plType == RhodesAdapter.EPlatformType.eEmu)
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
		rhodesLogHelper.configurePlatform(type);
		rhodesLogHelper.startLog(project);
	}
}

