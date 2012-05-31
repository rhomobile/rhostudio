package rhogenwizard.sdk.task;

import org.eclipse.core.runtime.IProgressMonitor;

import rhogenwizard.OSHelper;
import rhogenwizard.OSValidator;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.helper.ConsoleBuildAdapter;

public abstract class RubyTask extends RunTask
{
    protected final SysCommandExecutor m_executor;

    public RubyTask()
    {
        m_executor = new SysCommandExecutor();
        m_executor.setOutputLogDevice(new ConsoleBuildAdapter());
        m_executor.setErrorLogDevice(new ConsoleBuildAdapter());
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
    public void run(IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            throw new StoppedException();
        }

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                exec();
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
    }

    public static String getCommand(String name)
    {
        if (OSValidator.OSType.WINDOWS == OSValidator.detect())
        {
            return name + ".bat";
        }
        return name;
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
}
