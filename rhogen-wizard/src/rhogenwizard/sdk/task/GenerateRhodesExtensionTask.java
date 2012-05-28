package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhodesExtensionTask extends RhodesTask
{
    private final String m_workDir;
    private final String m_extName;

    public GenerateRhodesExtensionTask(String workDir, String extName)
    {
        m_workDir = workDir;
        m_extName = extName;
    }

    @Override
    protected void exec()
    {
        List<String> cmdLine = Arrays.asList(m_rhogenExe, "extension", m_extName);

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
