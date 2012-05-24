package rhogenwizard.sdk.task;

import javax.naming.directory.InvalidAttributesException;

import rhogenwizard.PlatformType;
import rhogenwizard.sdk.helper.TaskResultConverter;

public class BuildPlatformTask extends RakeTask
{
    public static final String platformType = "platform-type";

    @Override
    public void run()
    {
        try
        {
            if (m_taskParams == null || m_taskParams.size() == 0)
                throw new InvalidAttributesException(
                        "parameters data is invalid [BuildPlatformTask]");

            String workDir = (String) m_taskParams.get(IRunTask.workDir);
            PlatformType platformType =
                    (PlatformType) m_taskParams.get(CleanPlatformTask.platformType);

            runRakeTask(workDir, "device:" + platformType.toString() + ":production");
        }
        catch (Exception e)
        {
            Integer resCode = new Integer(TaskResultConverter.failCode);
            m_taskResult.put(resTag, resCode);
        }
    }
}
