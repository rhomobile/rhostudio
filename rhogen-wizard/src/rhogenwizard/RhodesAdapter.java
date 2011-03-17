package rhogenwizard;

import java.util.ArrayList;
import java.util.List;

public class RhodesAdapter 
{
	private static final String winRhogenFileName = "rhogen.bat";
	private static final String unixRhogenFileName = "rhogen";
	
	private String m_rhogenExe = null;
	private SysCommandExecutor m_executor = new SysCommandExecutor();
	
	public RhodesAdapter()
	{
		if (OSValidator.OSType.WINDOWS == OSValidator.detect()) 
		{
			 m_rhogenExe = winRhogenFileName;
		} 
		else
		{
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
		cmdLine.add(modelParams);
		cmdLine.add(modelName);
		
		m_executor.runCommand(cmdLine);
		
		return true;		
	}
}
