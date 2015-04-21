package rhogenwizard.sdk.task;

import org.eclipse.debug.core.model.IProcess;

public class SeqDebugTask extends SeqRunTask implements IDebugTask
{
    public static class Args
    {
        private final IRunTask[] m_allTasks;
        private final IDebugTask m_debugTask;
        
        public Args(IRunTask[] runTasks, IDebugTask debugTask)
        {
            m_allTasks = new IRunTask[runTasks.length + 1];
            for (int i = 0; i < runTasks.length; ++i)
            {
                m_allTasks[i] = runTasks[i];
            }
            m_allTasks[m_allTasks.length - 1] = debugTask;
            m_debugTask = debugTask;
        }
    }
    
    private final IDebugTask m_debugTask;

    public SeqDebugTask(Args args)
    {
        super(args.m_allTasks);
        m_debugTask = args.m_debugTask;
    }

    public SeqDebugTask(IRunTask runTask, IDebugTask debugTask)
    {
        this(new Args(new IRunTask[] { runTask }, debugTask));
    }

    @Override
    public IProcess getDebugProcess()
    {
        return m_debugTask.getDebugProcess();
    }
}
