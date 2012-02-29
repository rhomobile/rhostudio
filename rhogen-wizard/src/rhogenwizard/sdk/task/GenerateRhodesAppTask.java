package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.InvalidAttributesException;

import rhogenwizard.OSValidator;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhodesAppTask extends RhodesTask 
{	
	public static final String appName = "appname";

	public GenerateRhodesAppTask()
	{
		super();
	}
	
	@Override
	public void run() 
	{
		try 
		{
			m_taskResult.clear();
			
			if (m_taskParams == null || m_taskParams.size() == 0)
				throw new InvalidAttributesException("parameters data is invalid [GenerateRhodesAppTask]");		
			
			String workDir = (String) m_taskParams.get(this.workDir);
			String appName = (String) m_taskParams.get(this.appName);
			
			m_executor.setWorkingDirectory(workDir);
			
			List<String> cmdLine = new ArrayList<String>();
			cmdLine.add(m_rhogenExe);
			cmdLine.add("app");
			cmdLine.add(appName);
	
			int res = m_executor.runCommand(cmdLine);
			
			Integer resCode = new Integer(res);  
			m_taskResult.put(resTag, resCode);
		} 
		catch (Exception e) 
		{
			Integer resCode = new Integer(TaskResultConverter.failCode);  
			m_taskResult.put(resTag, resCode);		
		}
	}
}
