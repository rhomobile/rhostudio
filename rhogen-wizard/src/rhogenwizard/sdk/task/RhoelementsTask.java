package rhogenwizard.sdk.task;

import rhogenwizard.OSValidator;

public abstract class RhoelementsTask extends RakeTask 
{
	protected String m_rhoelExe = "rhoelements";
		
	public RhoelementsTask()
	{		
		if (OSValidator.OSType.WINDOWS == OSValidator.detect()) 
		{
			m_rhoelExe  = m_rhoelExe + ".bat";
		} 
	}
}
