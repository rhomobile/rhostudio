package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhoconnectAdapterTask extends RhoconnectTask
{
    public static final String sourceName = "source-name";

    @Override
    public void run()
    {
        m_taskResult.clear();

        try
        {
            if (m_taskParams == null || m_taskParams.size() == 0)
                throw new InvalidAttributesException(
                        "parameters data is invalid [GenerateRhoconnectAdapterTask]");

            String workDir = (String) m_taskParams.get(IRunTask.workDir);
            String sourceName =
                    (String) m_taskParams.get(GenerateRhoconnectAdapterTask.sourceName);

            m_executor.setWorkingDirectory(workDir);

            List<String> cmdLine = new ArrayList<String>();
            cmdLine.add(m_rhoConnectExe);
            cmdLine.add("source");
            cmdLine.add(sourceName);

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
