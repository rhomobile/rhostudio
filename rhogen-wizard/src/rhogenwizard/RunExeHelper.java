package rhogenwizard;

import java.util.ArrayList;
import java.util.List;

import rhogenwizard.constants.CommonConstants;

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
	
	static public RunExeHelper killBbSimulator()
	{
		try 
		{
			RunExeHelper hlpKillTask = new RunExeHelper("tasklist", true);
			List<String> params = new ArrayList<String>();
			params.add("/IM");
			params.add("fledge.exe");
			hlpKillTask.run(params);
			return hlpKillTask;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	static public String getSdkInfo()
	{
		try 
		{
			RunExeHelper hlpKillTask = new RunExeHelper("get-rhodes-info", false);
			List<String> params = new ArrayList<String>();
			params.add("--rhodes-path");
			String out = hlpKillTask.run(params);
			out = out.replaceAll("\\p{Cntrl}", "");  		
			return out;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;		
	}
	
	static public boolean checkRhodesVersion(String sdkVer)
	{
		try 
		{
			RunExeHelper runHelper = new RunExeHelper("get-rhodes-info", false);
			
			StringBuilder sb = new StringBuilder();
			sb.append("--rhodes-ver=");
			sb.append(sdkVer);
			
			List<String> cmdLine = new ArrayList<String>();
			cmdLine.add(sb.toString());
			
			String cmdOutput = runHelper.run(cmdLine); 
			
			cmdOutput = cmdOutput.replaceAll("\\p{Cntrl}", "");
			
			if (cmdOutput.equals(CommonConstants.okRhodesVersionFlag))
			{
				return true;
			}
			
			return false;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return false;		
	}
}
