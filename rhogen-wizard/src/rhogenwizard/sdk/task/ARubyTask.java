package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rhogenwizard.ILogDevice;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.helper.ConsoleBuildAdapter;

public class ARubyTask extends RubyTask
{
    private final SysCommandExecutor m_executor;

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

    public void disableConsole()
    {
        m_executor.setOutputLogDevice(nullLogDevice);
        m_executor.setErrorLogDevice(nullLogDevice);
    }

    @Override
    protected void exec()
    {
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
}
