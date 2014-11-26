package rhogenwizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;

import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.sdk.task.RubyExecTask;

class AppLogAdapter implements ILogDevice
{
	private static final int maxShowLines = 2*10*1000;
	
	private int                   m_currShowLines = 0;
	private ConsoleHelper.Console m_console = ConsoleHelper.getAppConsole();

	@Override
	public void log(String str) throws Exception 
	{
	    //m_console.show();
		
		m_currShowLines++;
		m_console.getStream().println(prepareString(str));
		
		if (m_currShowLines > maxShowLines)
		{
			m_currShowLines = 0;
			m_console.clear();
		}
	}
	
	private String prepareString(String message)
	{
		message = message.replaceAll("\\p{Cntrl}", " ");  		
		return message;
	}
}

interface ILogFileHelper
{
	void endWaitLog(IProject project, RunType type) throws Exception;
}

public class LogFileHelper implements ILogFileHelper
{
	class LogFileWaiter implements Runnable
	{
		private IProject 	   m_project     = null;
		private ILogFileHelper m_helper      = null;
		private String         m_logFilePath = null;
		private RunType        m_type        = null;
		
		public LogFileWaiter(IProject project, ILogFileHelper helper, String logFilePath, RunType type) throws Exception 
		{
			m_project     = project;
			m_helper      = helper;
			m_logFilePath = logFilePath;
			m_type        = type;
		}

		@Override
		public void run() 
		{
			try 
			{				
				while(true)
				{
					if (m_logFilePath == null)
						return;
					
					File logFile = new File(m_logFilePath);
					
					if (logFile.exists())
					{			
						m_helper.endWaitLog(m_project, m_type);
						return;
					}
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	/* ----------------------------------------------------------------- */
	
	private PlatformType       m_platformName = null;
	private AsyncStreamReader  m_appLogReader = null;
	private InputStream        m_logFileStream = null;
	private StringBuffer       m_logOutput = null;
	private AppLogAdapter      m_logAdapter = new AppLogAdapter();

	@Override
	protected void finalize() throws Throwable 
	{
		stopLog();
		super.finalize();
	}

	public void endWaitLog(IProject project, RunType type) throws Exception 
	{
		if (type == RunType.eRhoSimulator)
		{
			rhoSimLog(project);
		}
//		else
//		{
//			switch(m_platformName)
//			{
//			case eBb:
//				bbLog(project);
//				break;
//			}
//		}
	}
	
	public void startLog(PlatformType platformName, IProject project, RunType runType) throws Exception
	{
		m_platformName = platformName;
		
		if (runType == RunType.eRhoSimulator)
		{
			waitSimLog(project, runType);
		}
		else
		{
			switch(m_platformName)
			{
			case eWm:
				wmLog(project);
				break;
			case eAndroid:
				adnroidLog(project);
				break;
			case eIPhone:
				iphoneLog(project);
				break;
			case eWp7:
				wpLog(project);
				break;
			}
		}
	}
	
	private void waitSimLog(IProject project, RunType type) throws Exception
	{
        String logFilePath = getLogFilePath(project, "run:rhosimulator:get_log");
        Thread waitingLog = new Thread(new LogFileWaiter(project, this, logFilePath, type));
		waitingLog.start();
	}
	
	private void wpLog(IProject project) throws Exception
	{
        String logPath = getLogFilePath(project, "run:wp:get_log");
		
        if (logPath != null)
        {
             asyncFileRead(logPath);
        }
	}

	private void rhoSimLog(IProject project) throws Exception
	{
		String logPath = getLogFilePath(project, "run:rhosimulator:get_log");
		
		if (logPath != null)
		{
			asyncFileRead(logPath);
		}
	}
	
	private void wmLog(IProject project) throws Exception
	{
		String logPath = getLogFilePath(project, "run:wm:get_log");
		
		if (logPath != null)
		{
			asyncFileRead(logPath);
		}
	}
	
	private void iphoneLog(IProject project) throws Exception
	{
		String logPath = getLogFilePath(project, "run:iphone:get_log");
		
		if (logPath != null)
		{
			asyncFileRead(logPath);
		}
	}
	
	public void stopLog()
	{
		if (m_appLogReader != null)
		{
			m_appLogReader.stopReading();
		}
	}
	
	private void asyncFileRead(String logFilePath) throws FileNotFoundException
	{
		stopLog();
		
		File logFile = new File(logFilePath);
		m_logFileStream =  new FileInputStream(logFile);
		
		m_logOutput = new StringBuffer();
		m_appLogReader = new AsyncStreamReader(true, m_logFileStream, m_logOutput, m_logAdapter, "APPLOG");		
		m_appLogReader.start();
	}
	
	private void adnroidLog(IProject project) throws FileNotFoundException
	{
		AppYmlFile projectConfig = AppYmlFile.createFromProject(project);
		
		String logFilePath = project.getLocation().toOSString() + File.separatorChar + projectConfig.getAppLog(); 
		
		asyncFileRead(logFilePath);
	}
	
	private String getLogFilePath(IProject project, String taskName) throws Exception
	{
		RubyExecTask task = new RubyExecTask(project.getLocation().toOSString(), SysCommandExecutor.RUBY_BAT,
		    "rake", taskName);
		task.run();
		String output = task.getOutput();
		
		StringTokenizer st = new StringTokenizer(output, "\n");
		
		while (st.hasMoreTokens()) 
		{
			String token = st.nextToken();
			
			if (token.contains("log_file"))
			{
				String[] parts = token.split("=");
				
				if (parts.length < 2)
					return null;
				
				String logFile = parts[1];
				
				logFile = logFile.replaceAll("\\p{Cntrl}", ""); 
				
				return logFile;
			}
		}
		
		return null;
	}
}
