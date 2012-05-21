package rhogenwizard.sdk.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.helper.TaskResultConverter;

public class GenerateRhodesAppTask extends RhodesTask
{
    public interface Result
    {
        public Map<String, Object> waitFor() throws InterruptedException;
    }

    private class ResultImpl implements Result
    {
        private final SysCommandExecutor.Command m_command;

        public ResultImpl(SysCommandExecutor.Command command)
        {
            m_command = command;
        }

        @Override
        public Map<String, Object> waitFor() throws InterruptedException
        {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put(resTag, m_command.waitFor());
            return result;
        }
    }

    public static final String appName = "appname";

    @Override
    public void run()
    {
        try
        {
            m_taskResult = start().waitFor();
        }
        catch (Exception e)
        {
            m_taskResult.clear();
            m_taskResult.put(resTag, TaskResultConverter.failCode);
        }
    }

    public Result start() throws IOException
    {
        if (m_taskParams == null || m_taskParams.size() == 0)
            throw new IllegalArgumentException(
                    "parameters data is invalid [GenerateRhodesAppTask]");

        String workDir = (String) m_taskParams.get(GenerateRhodesAppTask.workDir);
        String appName = (String) m_taskParams.get(GenerateRhodesAppTask.appName);

        m_executor.setWorkingDirectory(workDir);

        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add(m_rhogenExe);
        cmdLine.add("app");
        cmdLine.add(appName);

        cmdLine = Arrays.asList("sleep", "50000");

        return new ResultImpl(m_executor.startCommand(cmdLine));
    }
}
