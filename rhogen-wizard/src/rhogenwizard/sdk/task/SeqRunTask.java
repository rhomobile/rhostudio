package rhogenwizard.sdk.task;

import org.eclipse.core.runtime.IProgressMonitor;

public class SeqRunTask extends RunTask
{
    private final RunTask m_tasks[];
    private boolean       m_isOk;

    public SeqRunTask(RunTask... tasks)
    {
        m_tasks = tasks;
        m_isOk = true;
    }

    @Override
    public boolean isOk()
    {
        return m_isOk;
    }

    @Override
    public void run(IProgressMonitor monitor)
    {
        for (RunTask task : m_tasks)
        {
            if (monitor.isCanceled())
            {
                throw new StoppedException();
            }

            task.run(monitor);
            if (!task.isOk())
            {
                m_isOk = false;
                break;
            }
        }
    }
}
