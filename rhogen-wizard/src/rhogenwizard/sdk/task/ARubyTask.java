package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.helper.ConsoleBuildAdapter;

public class ARubyTask extends RubyTask
{
    protected final SysCommandExecutor m_executor;

    private final String               m_workDir;
    private final List<String>         m_cmdLine;
    private Integer                    m_exitValue;

    public ARubyTask(String workDir, String commandName, String... args)
    {
        m_executor = new SysCommandExecutor();
        m_executor.setOutputLogDevice(new ConsoleBuildAdapter());
        m_executor.setErrorLogDevice(new ConsoleBuildAdapter());

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

    public String getOutput()
    {
        return m_executor.getCommandOutput();
    }

    public String getError()
    {
        return m_executor.getCommandError();
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
