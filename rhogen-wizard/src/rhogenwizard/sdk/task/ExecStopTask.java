package rhogenwizard.sdk.task;

import org.eclipse.core.runtime.IProgressMonitor;

public abstract class ExecStopTask extends RunTask
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
                ExecStopTask.this.exec();
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

    protected abstract void exec();

    protected abstract void stop();
}
