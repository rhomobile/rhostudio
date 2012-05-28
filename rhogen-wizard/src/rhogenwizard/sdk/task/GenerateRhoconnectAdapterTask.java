package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.List;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhoconnectAdapterTask extends RhoconnectTask
{
    private final String m_workDir;
    private final String m_sourceName;

    public GenerateRhoconnectAdapterTask(String workDir, String sourceName)
    {
        m_workDir = workDir;
        m_sourceName = sourceName;
    }

    @Override
    protected void exec()
    {
        List<String> cmdLine = Arrays.asList(m_rhoConnectExe, "source", m_sourceName);

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
