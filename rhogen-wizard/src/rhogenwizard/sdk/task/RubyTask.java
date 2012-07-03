package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import rhogenwizard.OSHelper;
import rhogenwizard.SysCommandExecutor;

public abstract class RubyTask extends RunTask
{
    protected final String                       m_workDir;
    protected final SysCommandExecutor.Decorator m_decorator;
    protected final List<String>                 m_cmdLine;

    public RubyTask(String workDir, SysCommandExecutor.Decorator decorator, String... args)
    {
        m_workDir = workDir;
        m_decorator = decorator;
        m_cmdLine = Arrays.asList(args);
    }

    @Override
    public void run(IProgressMonitor monitor)
    {
        final RuntimeException[] exceptions = { null };

        if (monitor.isCanceled())
        {
            throw new StoppedException();
        }

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    exec();
                }
                catch (RuntimeException e)
                {
                    exceptions[0] = e;
                }
            }
        });
        thread.start();

        while (thread.isAlive())
        {
            try
            {
                thread.join(100);
            }
            catch (InterruptedException e)
            {
                throw new StoppedException(e);
            }

            if (monitor.isCanceled())
            {
                stop();
                throw new StoppedException();
            }
        }

        if (exceptions[0] != null)
        {
            throw exceptions[0];
        }
    }

    protected abstract void exec();

    protected void stop()
    {
        try
        {
            OSHelper.killProcess("ruby");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected String showCommand()
    {
        return "\nPWD: " + showWorkingDir() + "\nCMD: " + showCommandLine() + "\n";
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
}
