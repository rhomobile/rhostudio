package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhoelementsAppTask extends RhoelementsTask
{
    public static final String appName = "appname";

    public GenerateRhoelementsAppTask(String workDir, String appName)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RunTask.workDir, workDir);
        params.put(GenerateRhoelementsAppTask.appName, appName);
        m_taskParams = params;
    }

    @Override
    protected void run()
    {
        if (m_taskParams == null || m_taskParams.size() == 0)
            throw new IllegalArgumentException("parameters data is invalid [GenerateRhoelementsAppTask]");

        String workDir = (String) m_taskParams.get(RunTask.workDir);
        String appName = (String) m_taskParams.get(GenerateRhoelementsAppTask.appName);

        List<String> cmdLine = Arrays.asList(m_rhoelExe, "app", appName);

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
