package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhodesSpecTask extends RhodesTask
{
    @Override
    public void run()
    {
        try
        {
            m_taskResult.clear();

            if (m_taskParams == null || m_taskParams.size() == 0)
                throw new InvalidAttributesException(
                        "parameters data is invalid [GenerateRhodesAppSpec]");

            String workDir = (String) m_taskParams.get(this.workDir);

            m_executor.setWorkingDirectory(workDir);

            List<String> cmdLine = new ArrayList<String>();
            cmdLine.add(m_rhogenExe);
            cmdLine.add("spec");

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
