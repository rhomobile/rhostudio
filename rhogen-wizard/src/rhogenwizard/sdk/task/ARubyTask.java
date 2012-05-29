package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rhogenwizard.sdk.helper.TaskResultConverter;

public class ARubyTask extends RubyTask
{
    private final String       m_workDir;
    private final List<String> m_cmdLine;

    public ARubyTask(String workDir, String commandName, String... args)
    {
        m_workDir = workDir;

        m_cmdLine = new ArrayList<String>();
        m_cmdLine.add(getCommand(commandName));
        m_cmdLine.addAll(Arrays.asList(args));
    }

    @Override
    protected void exec()
    {
        m_taskResult.clear();
        int result = TaskResultConverter.failCode;

        try
        {
            m_executor.setWorkingDirectory(m_workDir);
            result = m_executor.runCommand(m_cmdLine);
        }
        catch (Exception e)
        {
        }

        m_taskResult.put(resTag, result);
    }
}
