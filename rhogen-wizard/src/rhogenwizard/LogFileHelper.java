package rhogenwizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.console.MessageConsoleStream;

import rhogenwizard.buildfile.AppYmlFile;

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

public class LogFileHelper
{
	class LogFileWaiter implements Runnable
	{
		private IProject 	  m_project = null;
		private LogFileHelper m_helper = null;
		private String        m_logFilePath = null;

		public LogFileWaiter(IProject project, LogFileHelper helper, String taskName) throws Exception 
		{
			m_project = project;
			m_helper  = helper;
			
			if (m_project != null && m_helper != null)
			{
				m_logFilePath = m_helper.getLogFilePath(m_project, taskName);
			}
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
						m_helper.endWaitLog(m_project);
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
	
	private RhodesAdapter.EPlatformType m_platformName = null;
	private AsyncStreamReader 			m_appLogReader = null;
	private InputStream       			m_logFileStream = null;
	private StringBuffer     			m_logOutput = null;
	private AppLogAdapter               m_logAdapter = new AppLogAdapter();

	@Override
	protected void finalize() throws Throwable 
	{
		stopLog();
		super.finalize();
	}

	public void endWaitLog(IProject project) throws Exception 
	{
		switch(m_platformName)
		{
		case eBb:
			waitBbLog(project);
			break;
		case eEmu:
			rhoSimLog(project);
			break;
			
		}
	}

	public void configurePlatform(RhodesAdapter.EPlatformType platformName)
	{
		m_platformName = platformName;
	}
	
	public void startLog(IProject project) throws Exception
	{
		switch(m_platformName)
		{
		case eWm:
			wmLog(project);
			break;
		case eAndroid:
			adnroidLog(project);
			break;
		case eBb:
			waitBbLog(project);
			break;
		case eIPhone:
			iphoneLog(project);
			break;
		case eWp7:
			wpLog(project);
			break;
		case eEmu:
			waitSimLog(project);
			break;
			
		}
	}
	
	private void waitSimLog(IProject project) throws Exception
	{
		Thread waitingLog = new Thread(new LogFileWaiter(project, this, "run:rhosimulator:get_log"));
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
	
	private void bbLog(IProject project) throws Exception
	{
		String logPath = getLogFilePath(project,"run:bb:get_log");
	
		if (logPath != null)
		{
			asyncFileRead(logPath);
		}
	}
	
	private String getLogFilePath(IProject project, String taskName) throws Exception
	{
		RhodesAdapter rhodesExecutor = new RhodesAdapter();
		
		String output = rhodesExecutor.runRakeTask(project.getLocation().toOSString(), taskName);
		
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
	
	private void waitBbLog(IProject project) throws Exception
	{
		Thread waitingLog = new Thread(new LogFileWaiter(project, this, "run:bb:get_log"));
		waitingLog.start();
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
}
