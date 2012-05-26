package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhodesSpecTask extends RhodesTask
{
    public GenerateRhodesSpecTask(String workDir)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RunTask.workDir, workDir);
        m_taskParams = params;
    }

    @Override
    protected void exec()
    {
        if (m_taskParams == null || m_taskParams.size() == 0)
            throw new IllegalArgumentException("parameters data is invalid [GenerateRhodesAppSpec]");

        String workDir = (String) m_taskParams.get(RunTask.workDir);

        List<String> cmdLine = Arrays.asList(m_rhogenExe, "spec");

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
