package rhogenwizard.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore.Builder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.internal.events.BuildManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.internal.ui.stringsubstitution.ResourceSelector;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.ide.ResourceSelectionUtil;
import org.eclipse.ui.views.navigator.IResourceNavigator;
import org.eclipse.ui.views.navigator.ResourceNavigator;

import rhogenwizard.AsyncStreamReader;
import rhogenwizard.ConsoleHelper;
import rhogenwizard.ILogDevice;
import rhogenwizard.OSHelper;
import rhogenwizard.OSValidator;
import rhogenwizard.RhodesAdapter;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.builder.RhogenBuilder;
import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.buildfile.SdkYmlFile;

class AppLogAdapter implements ILogDevice
{
	MessageConsoleStream m_consoleStream = ConsoleHelper.getConsoleAppStream();

	@Override
	public void log(String str) 
	{
		if (null != m_consoleStream)
		{
			m_consoleStream.println(prepareString(str));
		}
	}
	
	private String prepareString(String message)
	{
		message = message.replaceAll("\\p{Cntrl}", " ");  		
		return message;
	}
}

public class RhogenLaunchDelegate extends LaunchConfigurationDelegate implements IDebugEventSetListener 
{		
	public static final String projectNameCfgAttribute = "project_name";
	public static final String platforrmCfgAttribute = "platform";
	public static final String platforrmDeviceCfgAttribute = "device";
	public static final String prjectLogFileName = "log_filename";
	
	private static RhodesAdapter rhodesAdapter = new RhodesAdapter();
	
	private String  m_projectName = null;
	private String  m_platformName = null;
	private boolean m_onDevice = false;
	private AtomicBoolean m_buildFinished = new AtomicBoolean();
	private StringBuffer m_logOutput = null;
	private AsyncStreamReader m_appLogReader = null;
	private AppLogAdapter m_logAdapter = new AppLogAdapter();
	private InputStream m_logFileStream = null;
	
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
			
			if (m_appLogReader != null)
			{
				m_appLogReader.stopReading();
			}
			
			m_projectName   = configuration.getAttribute(projectNameCfgAttribute, "");
			m_platformName  = configuration.getAttribute(platforrmCfgAttribute, "");
			
			if (configuration.getAttribute(platforrmDeviceCfgAttribute, "").equals("yes"))
			{
				m_onDevice = true;
			}
			
			final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(m_projectName);
			
			if (project == null || m_platformName == null || m_platformName.length() == 0)
			{
				throw new IllegalArgumentException();
			}
			
			Thread cancelingThread = new Thread(new Runnable() 
			{	
				@Override
				public void run() 
				{
					try 
					{
						rhodesAdapter.buildApp(project.getLocation().toOSString(), m_platformName, m_onDevice);
						setProcessFinished(true);
						startLogOutput(project);
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
			ConsoleHelper.consolePrint("Error - Platform and project name should be assigned");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
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
	
	private void startLogOutput(IProject project) throws FileNotFoundException
	{
		AppYmlFile projectConfig = AppYmlFile.createFromProject(project);
		
		String rhodesConfigPath  = projectConfig.getSdkPath() + "/" + SdkYmlFile.configName;
		SdkYmlFile sdkConfig = new SdkYmlFile(rhodesConfigPath);
		
		String logFilePath = sdkConfig.getAppName() + "/" + projectConfig.getAppLog(); 
		File a = new File(logFilePath);
		
		m_logFileStream =  new FileInputStream(a);
		
		m_logOutput = new StringBuffer();
		m_appLogReader = new AsyncStreamReader(m_logFileStream, m_logOutput, m_logAdapter, "APPLOG");		
		m_appLogReader.start();
	}
}

