package rhogenwizard.sdk.task;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public abstract class RunTask
{
    public static class StoppedException extends RuntimeException
    {
        private static final long serialVersionUID = 4771945946727616049L;

        public StoppedException()
        {
        }

        public StoppedException(Throwable e)
        {
            super(e);
        }
    }

    public static final String resTag  = "result-code";
    public static final String workDir = "workdir";

    public abstract void setData(Map<String, ?> data);

    public abstract void run();

    public abstract void stop();

    public abstract Map<String, ?> getResult();

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
                RunTask.this.run();
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

    public Job makeJob(String name)
    {
        return new Job(name)
        {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
                try
                {
                    RunTask.this.run(monitor);
                }
                catch (StoppedException e)
                {
                    return Status.CANCEL_STATUS;
                }
                return Status.OK_STATUS;
            }
        };
    }
}
