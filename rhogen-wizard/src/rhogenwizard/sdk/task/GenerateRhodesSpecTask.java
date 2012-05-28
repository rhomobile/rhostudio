package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.List;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhodesSpecTask extends RhodesTask
{
    private final String m_workDir;

    public GenerateRhodesSpecTask(String workDir)
    {
        m_workDir = workDir;
    }

    @Override
    protected void exec()
    {
        List<String> cmdLine = Arrays.asList(m_rhogenExe, "spec");

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
