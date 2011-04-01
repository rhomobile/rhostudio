package rhogenwizard.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import rhogenwizard.AsyncStreamReader;
import rhogenwizard.ConsoleHelper;
import rhogenwizard.LogFileHelper;
import rhogenwizard.OSHelper;
import rhogenwizard.RhodesAdapter;
import rhogenwizard.RhodesAdapter.EPlatformType;
import rhogenwizard.builder.RhogenBuilder;
import rhogenwizard.buildfile.AppYmlFile;


public class RhogenLaunchDelegate extends LaunchConfigurationDelegate implements IDebugEventSetListener 
{		
	public static final String projectNameCfgAttribute = "project_name";
	public static final String platforrmCfgAttribute = "platform";
	public static final String platforrmDeviceCfgAttribute = "device";
	public static final String prjectLogFileName = "log_filename";
	
	private static RhodesAdapter rhodesAdapter = new RhodesAdapter();
	private static LogFileHelper rhodesLogHelper = new LogFileHelper();
	
	private String            m_projectName = null;
	private String            m_platformName = null;
	private String			  m_appLogName = null; 
	private boolean           m_onDevice = false;
	private AtomicBoolean     m_buildFinished = new AtomicBoolean();
	
	private void setProcessFinished(boolean b)
	{
		m_buildFinished.set(b);
	}

	private boolean getProcessFinished()
	{
		return m_buildFinished.get();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@SuppressWarnings("deprecation")
	public synchronized void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, final IProgressMonitor monitor) throws CoreException 
	{
		try
		{
			setProcessFinished(false); 
			
			rhodesLogHelper.stopLog();
			
			m_projectName   = configuration.getAttribute(projectNameCfgAttribute, "");
			m_platformName  = configuration.getAttribute(platforrmCfgAttribute, "");
			m_appLogName    = configuration.getAttribute(prjectLogFileName, "");
			
			if (configuration.getAttribute(platforrmDeviceCfgAttribute, "").equals("yes"))
			{
				m_onDevice = true;
			}
			
			final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(m_projectName);
			
			if (project == null || m_platformName == null || m_platformName.length() == 0 || !project.isOpen())
			{
				throw new IllegalArgumentException("Error - Platform and project name should be assigned");
			}
						
			Thread cancelingThread = new Thread(new Runnable() 
			{	
				@Override
				public void run() 
				{
					try 
					{
						EPlatformType type = RhodesAdapter.convertPlatformFromDesc(m_platformName);
						rhodesAdapter.buildApp(project.getLocation().toOSString(), type, m_onDevice);
						setProcessFinished(true);
						startLogOutput(project, type);
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			});
			cancelingThread.start();
			
			while(true)
			{
				try 
			    {
					if (monitor.isCanceled()) 
				    {
						OSHelper.killProcess("ruby", "ruby.exe");
						return;
				    }
					
					if (getProcessFinished())
					{
						return;
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

