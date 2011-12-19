package rhogenwizard.sdk.task;

import rhogenwizard.OSValidator;
import rhogenwizard.SysCommandExecutor;

public abstract class RhoconnectTask extends RakeTask 
{
	protected String m_rhoConnectExe = "rhoconnect";

	public RhoconnectTask() 
	{		
		if (OSValidator.OSType.WINDOWS == OSValidator.detect()) 
		{
			m_rhoConnectExe = m_rhoConnectExe + ".bat";
		} 
	}
}
