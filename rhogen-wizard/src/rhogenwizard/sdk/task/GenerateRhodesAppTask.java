package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.InvalidAttributesException;

import rhogenwizard.OSValidator;
import rhogenwizard.SysCommandExecutor;

public class GenerateRhodesAppTask implements RunTask 
{	
	public static final String taskTag = "rhodes-gen";
	public static final String appName = "appname";
	
	private String              m_rhogenExe  = "rhodes";
	private Map<String, String> m_taskParams = null;
	private Map<String, String> m_taskResult = new HashMap<String, String>();
	private SysCommandExecutor  m_executor   = new SysCommandExecutor();
	
	public GenerateRhodesAppTask()
	{
//		m_executor.setOutputLogDevice(new RhodesLogAdapter());
//		m_executor.setErrorLogDevice(new RhodesLogAdapter());
		
		if (OSValidator.OSType.WINDOWS == OSValidator.detect()) 
		{
			 m_rhogenExe  = m_rhogenExe + ".bat";
		} 
	}
	
	@Override
	public void run() 
	{
		try 
		{
			m_taskResult.clear();
			
			if (m_taskParams == null || m_taskParams.size() == 0)
				throw new InvalidAttributesException("m_taskParams");		
			
			String workDir = m_taskParams.get(this.workDir);
			String appName = m_taskParams.get(this.appName);
			
			m_executor.setWorkingDirectory(workDir);
			
			List<String> cmdLine = new ArrayList<String>();
			cmdLine.add(m_rhogenExe);
			cmdLine.add("app");
			cmdLine.add(appName);
	
			int res = m_executor.runCommand(cmdLine);
			
			Integer resCode = new Integer(res);  
			m_taskResult.put(resTag, resCode.toString());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public void setData(Map<String, String> data) 
	{
		m_taskParams = data; 
	}

	@Override
	public Map<String, String> getResult() 
	{
		return m_taskResult;
	}

	@Override
	public String getTag() 
	{		
		return taskTag;
	}
}
