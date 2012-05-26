package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhoconnectAdapterTask extends RhoconnectTask
{
    public static final String sourceName = "source-name";

    public GenerateRhoconnectAdapterTask(String workDir, String sourceName)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RunTask.workDir, workDir);
        params.put(GenerateRhoconnectAdapterTask.sourceName, sourceName);
        m_taskParams = params;
    }

    @Override
    public void run()
    {
        if (m_taskParams == null || m_taskParams.size() == 0)
            throw new IllegalArgumentException("parameters data is invalid [GenerateRhoconnectAdapterTask]");

        String workDir = (String) m_taskParams.get(RunTask.workDir);
        String sourceName = (String) m_taskParams.get(GenerateRhoconnectAdapterTask.sourceName);

        List<String> cmdLine = Arrays.asList(m_rhoConnectExe, "source", sourceName);

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
