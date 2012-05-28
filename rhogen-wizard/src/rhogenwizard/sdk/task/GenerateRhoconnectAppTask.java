package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.List;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhoconnectAppTask extends RhoconnectTask
{
    private final String m_workDir;
    private final String m_appName;

    public GenerateRhoconnectAppTask(String workDir, String appName)
    {
        m_workDir = workDir;
        m_appName = appName;
    }

    @Override
    protected void exec()
    {
        List<String> cmdLine = Arrays.asList(m_rhoConnectExe, "app", m_appName);

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
