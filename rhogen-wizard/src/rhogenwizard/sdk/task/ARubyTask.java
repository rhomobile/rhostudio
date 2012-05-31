package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ARubyTask extends RubyTask
{
    private final String       m_workDir;
    private final List<String> m_cmdLine;
    private Integer            m_exitValue;

    public ARubyTask(String workDir, String commandName, String... args)
    {
        m_workDir = workDir;

        m_cmdLine = new ArrayList<String>();
        m_cmdLine.add(getCommand(commandName));
        m_cmdLine.addAll(Arrays.asList(args));

        m_exitValue = null;
    }

    @Override
    public boolean isOk()
    {
        if (m_exitValue == null)
        {
            throw new IllegalStateException("The task is not finished yet.");
        }
        return m_exitValue == 0;
    }

    public int getExitValue()
    {
        if (m_exitValue == null)
        {
            throw new IllegalStateException("The task is not finished yet.");
        }
        return m_exitValue;
    }

    @Override
    protected void exec()
    {
        int exitValue = -1;

        try
        {
            m_executor.setWorkingDirectory(m_workDir);
            exitValue = m_executor.runCommand(m_cmdLine);
        }
        catch (Exception e)
        {
        }

        m_exitValue = exitValue;
    }
}
