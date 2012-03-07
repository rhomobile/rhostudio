package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhodesExtensionTask extends RhodesTask 
{
	public static final String extName = "ext-name";
	
	@Override
	public void run() 
	{
		try 
		{
			m_taskResult.clear();
			
			if (m_taskParams == null || m_taskParams.size() == 0)
				throw new InvalidAttributesException("parameters data is invalid [GenerateRhodesExtensionTask]");		
			
			String workDir = (String) m_taskParams.get(this.workDir);
			String extName = (String) m_taskParams.get(this.extName);		
			
			m_executor.setWorkingDirectory(workDir);
			
			List<String> cmdLine = new ArrayList<String>();
			cmdLine.add(m_rhogenExe);
			cmdLine.add("extension");
			cmdLine.add(extName);
	
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
