package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.List;

import rhogenwizard.PlatformType;
import rhogenwizard.sdk.helper.TaskResultConverter;

public class CleanPlatformTask extends RakeTask
{
    private final String       m_workDir;
    private final PlatformType m_platformType;

    public CleanPlatformTask(String workDir, PlatformType platformType)
    {
        m_workDir = workDir;
        m_platformType = platformType;
    }

    @Override
    protected void exec()
    {
        List<String> cmdLine = Arrays.asList(m_rakeExe, "clean:" + m_platformType);

        m_taskResult.clear();
        int result = TaskResultConverter.failCode;

        try
        {
            m_executor.setWorkingDirectory(m_workDir);
            result = m_executor.runCommand(cmdLine);
        }
        catch (Exception e)
        {
        }

        m_taskResult.put(resTag, result);
    }
}
