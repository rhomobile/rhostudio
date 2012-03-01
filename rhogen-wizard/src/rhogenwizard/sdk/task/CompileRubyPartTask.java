package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;

import rhogenwizard.ILogDevice;
import rhogenwizard.OSValidator;
import rhogenwizard.PlatformType;
import rhogenwizard.sdk.helper.TaskResultConverter;

class OutputAdapter implements ILogDevice
{
	private List<String> m_outputStrings = new ArrayList<String>();
	
	@Override
	public void log(String str) 
	{
		m_outputStrings.add(str);
	}	
	
	List<String> getOutput()
	{
		return m_outputStrings;
	}
	
	public void cleanOutput()
	{
		m_outputStrings.clear();
	}
}

public class CompileRubyPartTask extends RakeTask  
{
	public static final String platformType = "platform-type"; // wm, wp, iphone, etc
	public static final String outStrings   = "cmd-output";
	
	private OutputAdapter m_outputHolder = new OutputAdapter();
	
	public CompileRubyPartTask()
	{
		m_executor.setErrorLogDevice(m_outputHolder);	 
	}
	
	@Override
	public void run() 
	{
		try
		{
			if (m_taskParams == null || m_taskParams.size() == 0)
				throw new InvalidAttributesException("parameters data is invalid [CompileRubyPartTask]");		
	
			m_outputHolder.cleanOutput();
			
			String       workDir = (String) m_taskParams.get(IRunTask.workDir);
			PlatformType plType  = (PlatformType) m_taskParams.get(platformType); 
			
			StringBuilder sb = new StringBuilder();
			sb.append("build:");
			sb.append(plType.toString());
			sb.append(":rhobundle");
			
			List<String> cmdLine = new ArrayList<String>();
			cmdLine.add(m_rakeExe);
			cmdLine.add(sb.toString());
				
			m_executor.setWorkingDirectory(workDir);
			
			int res = m_executor.runCommand(cmdLine);
			
			Integer resCode = new Integer(res);  
			m_taskResult.put(resTag, resCode);
			m_taskResult.put(outStrings, m_outputHolder.getOutput());			
		}
		catch (Exception e) 
		{
			Integer resCode = new Integer(TaskResultConverter.failCode);  
			m_taskResult.put(resTag, resCode);	
		}
	}
}
