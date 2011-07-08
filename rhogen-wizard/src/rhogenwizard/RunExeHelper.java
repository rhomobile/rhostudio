package rhogenwizard;

import java.util.ArrayList;
import java.util.List;

public class RunExeHelper 
{
	String 			   m_exeName = null; 
	SysCommandExecutor m_executor = null;
	
	public RunExeHelper(String exeName, boolean isExe)
	{
		m_executor = new SysCommandExecutor();
		
		if (OSHelper.isWindows())
		{
			if (isExe)
			{
				m_exeName = exeName + ".exe";
			}
			else
			{
				m_exeName = exeName + ".bat";
			}
		}
		else 
		{
			m_exeName = exeName;
		}
	}
	
	public String run(List<String> params) throws Exception
	{
		List<String> programParams = new ArrayList<String>();
		
		programParams.add(m_exeName);
		
		for (String param : params)
		{
			programParams.add(param);
		}

		m_executor.runCommand(programParams);
		
		return m_executor.getCommandOutput();
	}
	
	static public void killBbSimulator()
	{
		try 
		{
			RunExeHelper hlpKillTask = new RunExeHelper("tasklist", true);
			List<String> params = new ArrayList<String>();
			params.add("/IM");
			params.add("fledge.exe");
			hlpKillTask.run(params);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
