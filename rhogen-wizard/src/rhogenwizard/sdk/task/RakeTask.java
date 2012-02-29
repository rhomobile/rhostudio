package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rhogenwizard.OSValidator;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.helper.ConsoleBuildAdapter;

public abstract class RakeTask implements IRunTask
{
	protected String             m_rakeExe    = "rake";	
	protected SysCommandExecutor m_executor   = new SysCommandExecutor();
	protected Map<String, ?>     m_taskParams = null;
	protected Map                m_taskResult = new HashMap();	

	public RakeTask()
	{
		m_executor.setOutputLogDevice(new ConsoleBuildAdapter());
		m_executor.setErrorLogDevice(new ConsoleBuildAdapter());
		
		if (OSValidator.OSType.WINDOWS == OSValidator.detect()) 
		{
			m_rakeExe = m_rakeExe + ".bat";   
		} 
	}
	
	@Override
	public void setData(Map<String, ?> data) 
	{
		m_taskParams = data; 
	}

	@Override
	public Map<String, ?> getResult() 
	{
		return m_taskResult;
	}
	
	public String runRakeTask(String workDir, String taskName) throws Exception
	{
		m_executor.setWorkingDirectory(workDir);
		
		List<String> cmdLine = new ArrayList<String>();
		cmdLine.add(m_rakeExe);
		cmdLine.add(taskName);
		
		m_executor.runCommand(cmdLine);
		
		return m_executor.getCommandOutput();
	}
}
