package rhogenwizard.sdk.task;

import rhogenwizard.OSValidator;
import rhogenwizard.SysCommandExecutor;

public abstract class RhoConnectTask implements RunTask 
{
	protected String             m_rhoExe   = "rhoconnect";
	protected SysCommandExecutor m_executor = new SysCommandExecutor();

	public RhoConnectTask() 
	{		
		if (OSValidator.OSType.WINDOWS == OSValidator.detect()) 
		{
			m_rhoExe = m_rhoExe + ".bat";
		} 
	}
}
