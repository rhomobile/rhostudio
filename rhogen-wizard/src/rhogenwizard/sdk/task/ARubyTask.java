package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.ILogDevice;
import rhogenwizard.SysCommandExecutor;

public class ARubyTask extends RubyTask
{
    private final SysCommandExecutor m_executor;
    private ILogDevice               m_logDevice;

    private final String             m_workDir;
    private final List<String>       m_cmdLine;
    private Integer                  m_exitValue;

    private static ILogDevice        nullLogDevice = new ILogDevice()
                                                   {
                                                       @Override
                                                       public void log(String str)
                                                       {
                                                       }
                                                   };

    public ARubyTask(String workDir, String commandName, String... args)
    {
        m_executor = new SysCommandExecutor();

        m_logDevice = getBuildLogDevice();

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
        m_logDevice = nullLogDevice;
    }

    @Override
    protected void exec()
    {
        m_executor.setOutputLogDevice(m_logDevice);
        m_executor.setErrorLogDevice(m_logDevice);

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
    }

    private static ILogDevice getBuildLogDevice()
    {
        return new ILogDevice()
        {
            private final ConsoleHelper.Stream m_stream = ConsoleHelper.getBuildConsoleStream();

            @Override
            public void log(String str)
            {
                m_stream.println(str.replaceAll("\\p{Cntrl}", " "));
            }
        };
    }
}
