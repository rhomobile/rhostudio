package rhogenwizard.sdk.task;

import org.eclipse.core.runtime.IProgressMonitor;

import rhogenwizard.OSHelper;
import rhogenwizard.OSValidator;

public abstract class RubyTask extends RunTask
{
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
