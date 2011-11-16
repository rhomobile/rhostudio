package rhogenwizard.sdk.task;

import javax.naming.directory.InvalidAttributesException;

public class CleanPlatformTask extends RakeTask 
{
	public static final String taskTag      = "clean-platform";
	public static final String platformType = "platform-type";
	
	@Override
	public String getTag() 
	{
		return taskTag;
	}

	@Override
	public void run() 
	{
		try 
		{
			if (m_taskParams == null || m_taskParams.size() == 0)
				throw new InvalidAttributesException("parameters data is invalid [CleanPlatformTask]");		
			
			String workDir      = (String) m_taskParams.get(this.workDir);
			String platformType = (String) m_taskParams.get(this.platformType);

			runRakeTask(workDir, "clean:" + platformType.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
