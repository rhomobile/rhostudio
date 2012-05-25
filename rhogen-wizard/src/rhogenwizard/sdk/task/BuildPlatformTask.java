package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rhogenwizard.PlatformType;
import rhogenwizard.sdk.helper.TaskResultConverter;

public class BuildPlatformTask extends RakeTask
{
    public static final String platformType = "platform-type";

    public BuildPlatformTask(String workDir, PlatformType platformType)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(IRunTask.workDir, workDir);
        params.put(BuildPlatformTask.platformType, platformType);
        m_taskParams = params;
    }

    @Override
    public void run()
    {
        if (m_taskParams == null || m_taskParams.size() == 0)
            throw new IllegalArgumentException("parameters data is invalid [BuildPlatformTask]");

        String workDir = (String) m_taskParams.get(IRunTask.workDir);
        PlatformType platformType = (PlatformType) m_taskParams.get(BuildPlatformTask.platformType);

        List<String> cmdLine = Arrays.asList(m_rakeExe, "device:" + platformType + ":production");

        m_taskResult.clear();
        int result = TaskResultConverter.failCode;
        
        try
        {
            m_executor.setWorkingDirectory(workDir);
            result = m_executor.runCommand(cmdLine);
        }
        catch (Exception e)
        {
        }
        
        m_taskResult.put(resTag, result);
    }
}
