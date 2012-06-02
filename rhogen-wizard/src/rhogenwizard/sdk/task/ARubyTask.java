package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.ILogDevice;
import rhogenwizard.SysCommandExecutor;

public class ARubyTask extends RubyTask
{
    private final SysCommandExecutor    m_executor;
    private ConsoleHelper.Stream        m_stream;

    private final String                m_workDir;
    private final List<String>          m_cmdLine;
    private Integer                     m_exitValue;

    private static ConsoleHelper.Stream nullStream = new ConsoleHelper.Stream()
                                                   {
                                                       @Override
                                                       public void println(String message)
                                                       {
                                                       }

                                                       @Override
                                                       public void println()
                                                       {
                                                       }

                                                       @Override
                                                       public void print(String message)
                                                       {
                                                       }
                                                   };

    public ARubyTask(String workDir, String commandName, String... args)
    {
        m_executor = new SysCommandExecutor();

        m_stream = ConsoleHelper.getBuildConsoleStream();

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

    public void disableConsole()
    {
        m_stream = nullStream;
    }

    @Override
    protected void exec()
    {
        m_stream.print("\nPWD: " + showWorkingDir() + "\nCMD: " + showCommandLine() + "\n");

        ILogDevice logDevice = getLogDevice(m_stream);
        m_executor.setOutputLogDevice(logDevice);
        m_executor.setErrorLogDevice(logDevice);

        int exitValue = -1;

        m_executor.setWorkingDirectory(m_workDir);
        try
        {
            exitValue = m_executor.runCommand(m_cmdLine);
        }
        catch (Exception e)
        {
        }

        m_exitValue = exitValue;

        m_stream.print("RET: " + m_exitValue + "\n");
    }

    private String showWorkingDir()
    {
        return m_workDir;
    }

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
