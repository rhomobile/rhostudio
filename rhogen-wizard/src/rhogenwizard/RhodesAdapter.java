package rhogenwizard;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.console.MessageConsoleStream;

class RhodesLogAdapter implements ILogDevice
{
	MessageConsoleStream m_consoleStream = ConsoleHelper.getConsoleMsgStream();
	
	@Override
	public void log(String str) 
	{
		if (null != m_consoleStream)
		{
			m_consoleStream.println(str);
		}
	}
}

public class RhodesAdapter 
{
	private static final String winRhogenFileName = "rhogen.bat";
	private static final String unixRhogenFileName = "rhogen";
	private static final String winRakeFileName = "rake.bat";
	private static final String unixRakeFileName = "rake";
		
	public static final String platformWinMobile = "wm";
	public static final String platformAdroid = "android";
	public static final String platformBlackBerry = "bb";
	public static final String platformIPhone = "iphone";
	public static final String platformSymbian = "symbian";
	
	private String m_rhogenExe = null; 
	private String m_rakeExe = null;
	private SysCommandExecutor m_executor = new SysCommandExecutor();
	
	public RhodesAdapter()
	{
		m_executor.setOutputLogDevice(new RhodesLogAdapter());
		
		if (OSValidator.OSType.WINDOWS == OSValidator.detect()) 
		{
			 m_rakeExe   = winRakeFileName;   
			 m_rhogenExe = winRhogenFileName;
		} 
		else
		{
			m_rakeExe   = unixRakeFileName;
			m_rhogenExe = unixRhogenFileName;
		}
	}
	
	public boolean generateApp(BuildInfoHolder holder) throws Exception
	{
		m_executor.setWorkingDirectory(holder.getProjectLocationPath().toOSString());
		
		List<String> cmdLine = new ArrayList<String>();
		cmdLine.add(m_rhogenExe);
		cmdLine.add("app");
		cmdLine.add(holder.appName);
		
		m_executor.runCommand(cmdLine);
		
		return true;		
	}
	
	public boolean generateModel(String workDir, String modelName, String modelParams) throws Exception
	{
		m_executor.setWorkingDirectory(workDir);
		
		List<String> cmdLine = new ArrayList<String>();
		cmdLine.add(m_rhogenExe);
		cmdLine.add("model");
		cmdLine.add(modelName);
		cmdLine.add(modelParams);
		
		m_executor.runCommand(cmdLine);
		
		return true;		
	}
	
	public boolean buildApp(String workDir, String platformName) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		sb.append("build:");
		sb.append(platformName);
		
		m_executor.setWorkingDirectory(workDir);
		
		List<String> cmdLine = new ArrayList<String>();
		cmdLine.add(m_rakeExe);
		cmdLine.add(sb.toString());
		
		m_executor.runCommand(cmdLine);
		
		return true;		
	}
}
