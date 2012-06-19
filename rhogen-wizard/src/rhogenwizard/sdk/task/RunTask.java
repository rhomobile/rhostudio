package rhogenwizard.sdk.task;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public abstract class RunTask
{
    public static class StoppedException extends RuntimeException
    {
        private static final long serialVersionUID = -4702661222816646561L;

        public StoppedException()
        {
        }

        public StoppedException(Throwable e)
        {
            super(e);
        }
    }

    public abstract boolean isOk();

    public void run()
    {
        run(new NullProgressMonitor());
    }

    public abstract void run(IProgressMonitor monitor);

    /**
     * @param name
     * @return associate Job object with task
     */
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
