package rhogenwizard.sdk.task;

import javax.naming.directory.InvalidAttributesException;

import rhogenwizard.PlatformType;

public class CleanAllPlatfromTask extends RakeTask 
{
	public static final String taskTag = "clean-platform";
	
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
				throw new InvalidAttributesException("parameters data is invalid [RunDebugRhoconnectAppTask]");		
			
			String workDir = (String) m_taskParams.get(this.workDir);

			runRakeTask(workDir, "clean:" + PlatformType.platformAdroid);
			runRakeTask(workDir, "clean:" + PlatformType.platformBlackBerry);
			runRakeTask(workDir, "clean:" + PlatformType.platformIPhone);
			runRakeTask(workDir, "clean:" + PlatformType.platformWinMobile);
			runRakeTask(workDir, "clean:" + PlatformType.platformWp7);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
