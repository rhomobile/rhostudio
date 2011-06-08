package rhogenwizard;

import java.util.ArrayList;
import java.util.List;

public class RunExeHelper 
{
	String 			   m_exeName = null; 
	SysCommandExecutor m_executor = null;
	
	public RunExeHelper(String exeName)
	{
		m_executor = new SysCommandExecutor();
		
		if (OSHelper.isWindows())
		{
			m_exeName = exeName + ".bat";
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
}
