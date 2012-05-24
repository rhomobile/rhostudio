package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.directory.InvalidAttributesException;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhoconnectAppTask extends RhoconnectTask
{
    public static final String appName = "app-name";

    @Override
    public void run()
    {
        m_taskResult.clear();

        try
        {
            if (m_taskParams == null || m_taskParams.size() == 0)
                throw new InvalidAttributesException(
                        "parameters data is invalid [GenerateRhoconnectAppTask]");

            String workDir = (String) m_taskParams.get(this.workDir);
            String appName = (String) m_taskParams.get(this.appName);

            m_executor.setWorkingDirectory(workDir);

            List<String> cmdLine = new ArrayList<String>();
            cmdLine.add(m_rhoConnectExe);
            cmdLine.add("app");
            cmdLine.add(appName);

            int res = m_executor.runCommand(cmdLine);

            Integer resCode = new Integer(res);

            m_taskResult.put(resTag, resCode);
        }
        catch (Exception e)
        {
            Integer resCode = new Integer(TaskResultConverter.failCode);
            m_taskResult.put(resTag, resCode);
        }
    }
}
