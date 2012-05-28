package rhogenwizard.sdk.task;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

public class SeqRunTask extends RunTask
{
    private final RunTask m_tasks[];

    public SeqRunTask(RunTask... tasks)
    {
        m_tasks = tasks;
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
        }
    }

    @Override
    public Map<String, ?> getResult()
    {
        if (m_tasks.length == 0)
        {
            return new HashMap<String, Object>();
        }
        return m_tasks[m_tasks.length - 1].getResult();
    }
}
