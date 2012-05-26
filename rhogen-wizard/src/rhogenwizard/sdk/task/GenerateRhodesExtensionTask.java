package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhodesExtensionTask extends RhodesTask
{
    public static final String extName = "ext-name";

    public GenerateRhodesExtensionTask(String workDir, String extName)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RunTask.workDir, workDir);
        params.put(GenerateRhodesExtensionTask.extName, extName);
        m_taskParams = params;
    }

    @Override
    protected void exec()
    {
        if (m_taskParams == null || m_taskParams.size() == 0)
            throw new IllegalArgumentException("parameters data is invalid [GenerateRhodesExtensionTask]");

        String workDir = (String) m_taskParams.get(RunTask.workDir);
        String extName = (String) m_taskParams.get(GenerateRhodesExtensionTask.extName);

        List<String> cmdLine = Arrays.asList(m_rhogenExe, "extension", extName);

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
