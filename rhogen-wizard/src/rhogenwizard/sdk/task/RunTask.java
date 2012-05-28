package rhogenwizard.sdk.task;

import java.util.Map;

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

    public static final String resTag  = "result-code";
    public static final String workDir = "workdir";

    public abstract Map<String, ?> getResult();

    public void run()
    {
        run(new NullProgressMonitor());
    }

    public abstract void run(IProgressMonitor monitor);

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
