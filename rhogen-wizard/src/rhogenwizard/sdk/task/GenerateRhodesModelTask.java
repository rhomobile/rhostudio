package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.naming.directory.InvalidAttributesException;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhodesModelTask extends RhodesTask
{
	public static final String modelName = "model-name";
	public static final String modelFields = "model-fields";
	
	public GenerateRhodesModelTask()
	{
		super();
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
	
	@Override
	public void run() 
	{
		try 
		{
			m_taskResult.clear();
			
			if (m_taskParams == null || m_taskParams.size() == 0)
				throw new InvalidAttributesException("parameters data is invalid [GenerateRhodesModelTask]");		
			
			String workDir = (String) m_taskParams.get(this.workDir);
			String modelName = (String) m_taskParams.get(this.modelName);
			String modelFields = (String) m_taskParams.get(this.modelFields);
			
			modelFields = prepareModelAttributes(modelFields);
			
			m_executor.setWorkingDirectory(workDir);
			
			List<String> cmdLine = new ArrayList<String>();
			cmdLine.add(m_rhogenExe);
			cmdLine.add("model");
			cmdLine.add(modelName);
			cmdLine.add(modelFields);
	
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
