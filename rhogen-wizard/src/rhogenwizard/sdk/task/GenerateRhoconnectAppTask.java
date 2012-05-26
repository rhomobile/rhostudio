package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhoconnectAppTask extends RhoconnectTask
{
    public static final String appName = "app-name";

    public GenerateRhoconnectAppTask(String workDir, String appName)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RunTask.workDir, workDir);
        params.put(GenerateRhoconnectAppTask.appName, appName);
        m_taskParams = params;
    }

    @Override
    protected void exec()
    {
        if (m_taskParams == null || m_taskParams.size() == 0)
            throw new IllegalArgumentException("parameters data is invalid [GenerateRhoconnectAppTask]");

        String workDir = (String) m_taskParams.get(RunTask.workDir);
        String appName = (String) m_taskParams.get(GenerateRhoconnectAppTask.appName);

        List<String> cmdLine = Arrays.asList(m_rhoConnectExe, "app", appName);

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
