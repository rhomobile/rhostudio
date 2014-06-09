package rhogenwizard.sdk.task;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public abstract class RunTask implements IRunTask
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

    public void run()
    {
        run(new NullProgressMonitor());
    }

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
    
    public void runAndWaitJob(String name) throws InterruptedException
    {
        Job theJob = makeJob(name);
        theJob.schedule();
        theJob.join();
    }
}
