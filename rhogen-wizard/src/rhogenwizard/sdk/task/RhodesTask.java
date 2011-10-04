package rhogenwizard.sdk.task;

import rhogenwizard.OSValidator;
import rhogenwizard.SysCommandExecutor;

public abstract class RhodesTask implements RunTask 
{
	protected String             m_rhogenExe  = "rhodes";
	protected SysCommandExecutor m_executor   = new SysCommandExecutor();
	
	public RhodesTask()
	{
//		m_executor.setOutputLogDevice(new RhodesLogAdapter());
//		m_executor.setErrorLogDevice(new RhodesLogAdapter());
		
		if (OSValidator.OSType.WINDOWS == OSValidator.detect()) 
		{
			 m_rhogenExe  = m_rhogenExe + ".bat";
		} 
	}
	
}
