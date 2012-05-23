package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhodesAppTask extends RhodesTask
{
    public static final String appName = "appname";

    public GenerateRhodesAppTask(String workDir, String appName)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RakeTask.workDir, workDir);
        params.put(GenerateRhodesAppTask.appName, appName);
        m_taskParams = params;
    }

    @Override
    public void run()
    {
        if (m_taskParams == null || m_taskParams.size() == 0)
            throw new IllegalArgumentException(
                    "parameters data is invalid [GenerateRhodesAppTask]");

        String workDir = (String) m_taskParams.get(RakeTask.workDir);
        String appName = (String) m_taskParams.get(GenerateRhodesAppTask.appName);

        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add(m_rhogenExe);
        cmdLine.add("app");
        cmdLine.add(appName);

        cmdLine = Arrays.asList("sleep", "3600");

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
