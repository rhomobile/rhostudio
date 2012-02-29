package rhogenwizard.sdk.task;

import javax.naming.directory.InvalidAttributesException;

import rhogenwizard.PlatformType;
import rhogenwizard.sdk.helper.TaskResultConverter;

public class CleanAllPlatfromTask extends RakeTask 
{
	public static final String taskTag = "clean-all-platform";
	
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
				throw new InvalidAttributesException("parameters data is invalid [CleanAllPlatfromTask]");		
			
			String workDir = (String) m_taskParams.get(IRunTask.workDir);

			runRakeTask(workDir, "clean:" + PlatformType.platformAdroid);
			runRakeTask(workDir, "clean:" + PlatformType.platformBlackBerry);
			runRakeTask(workDir, "clean:" + PlatformType.platformIPhone);
			runRakeTask(workDir, "clean:" + PlatformType.platformWinMobile);
			runRakeTask(workDir, "clean:" + PlatformType.platformWp7);
		}
		catch (Exception e)
		{
			Integer resCode = new Integer(TaskResultConverter.failCode);  
			m_taskResult.put(resTag, resCode);
		}
	}
}
