package rhogenwizard.sdk.task;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.ILogDevice;
import rhogenwizard.SysCommandExecutor;

public class RubyExecTask extends RubyTask
{
    private final SysCommandExecutor m_executor;
    private ConsoleHelper.Console    m_console;

    private Integer                  m_exitValue;

    public RubyExecTask(String workDir, SysCommandExecutor.Decorator decorator, String... args)
    {
        super(workDir, decorator, args);

        m_executor = new SysCommandExecutor();
        m_console  = ConsoleHelper.getBuildConsole();

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

    public void disableConsole()
    {
        m_console = ConsoleHelper.nullConsole;
    }

    @Override
    protected void exec()
    {
        m_console.show();

        ConsoleHelper.Stream stream = m_console.getStream();

        stream.print(showCommand());

        m_executor.setOutputLogDevice(getLogDevice(m_console.getOutputStream()));
        m_executor.setErrorLogDevice(getLogDevice(m_console.getErrorStream()));

        int exitValue = -1;

        m_executor.setWorkingDirectory(m_workDir);

        try
        {
            exitValue = m_executor.runCommand(m_decorator, m_cmdLine);
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
        }

        m_exitValue = exitValue;

        stream.print("RET: " + m_exitValue + "\n");
    }

    @SuppressWarnings("unused")
    private String showWorkingDir()
    {
        return m_workDir;
    }

    @SuppressWarnings("unused")
    private String showCommandLine()
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (String item : m_cmdLine)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                sb.append(' ');
            }

            sb.append(item);
        }

        return sb.toString();
    }

    private static ILogDevice getLogDevice(final ConsoleHelper.Stream stream)
    {
        return new ILogDevice()
        {
            @Override
            public void log(String str)
            {
                stream.println(str.replaceAll("\\p{Cntrl}", " "));
            }
        };
    }
}
