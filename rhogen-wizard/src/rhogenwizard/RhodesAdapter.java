package rhogenwizard;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.ui.console.MessageConsoleStream;

class RhodesLogAdapter implements ILogDevice
{
	MessageConsoleStream m_consoleStream = ConsoleHelper.getConsoleMsgStream();

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
	
	public void generateApp(BuildInfoHolder holder) throws Exception
	{
		m_executor.setWorkingDirectory(holder.getProjectLocationPath().toOSString());
		
		List<String> cmdLine = new ArrayList<String>();
		cmdLine.add(m_rhogenExe);
		cmdLine.add("app");
		cmdLine.add(holder.appName);
		
		m_executor.runCommand(cmdLine);
	}
	
	public void generateModel(String workDir, String modelName, String modelParams) throws Exception
	{
		m_executor.setWorkingDirectory(workDir);
		
		modelParams = prepareModelAttributes(modelParams);
		
		List<String> cmdLine = new ArrayList<String>();
		cmdLine.add(m_rhogenExe);
		cmdLine.add("model");
		cmdLine.add(modelName);
		cmdLine.add(modelParams);
		
		m_executor.runCommand(cmdLine);
	}
	
	public void buildApp(String workDir, String platformName) throws Exception
	{
		ConsoleHelper.consolePrint("build started");
		
		StringBuilder sb = new StringBuilder();
		sb.append("run:");
		sb.append(platformName);
		
		m_executor.setWorkingDirectory(workDir);
		
		List<String> cmdLine = new ArrayList<String>();
		cmdLine.add(m_rakeExe);
		cmdLine.add(sb.toString());
		
		m_executor.runCommand(cmdLine);		
	}
	
	private String prepareModelAttributes(String modelAttr)
	{
		StringBuilder   sb = new StringBuilder();
		StringTokenizer st = new StringTokenizer(modelAttr, ",");
		
		while (st.hasMoreTokens()) 
		{
			String token = st.nextToken();
			
			token = token.trim();
			token = token.replace(' ', '_');
			
			sb.append(token);
			
			if (st.hasMoreTokens())
			{
				sb.append(",");
			}
		}
		
		return sb.toString();
	}
}
