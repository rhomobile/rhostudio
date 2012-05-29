package rhogenwizard.sdk.task;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

public class RunDebugRhoconnectAppTask extends RunTask
{
    private final RunTask m_task;
    private IProcess      m_debugProcess;

    public RunDebugRhoconnectAppTask(final String workDir, final String appName, final ILaunch launch)
    {
        RunTask redisStartbgTask = new ARubyTask(workDir, "rake", "redis:startbg");

        RunTask rhoconnectStartdebugTask = new RubyTask()
        {
            @Override
            public boolean isOk()
            {
                return m_debugProcess != null;
            }

            @Override
            protected void exec()
            {
                String[] commandLine = { getCommand("rake"), "rhoconnect:startdebug" };

                Process process;
                try
                {
                    process = DebugPlugin.exec(commandLine, new File(workDir));
                }
                catch (CoreException e)
                {
                    return;
                }

                m_debugProcess = DebugPlugin.newProcess(launch, process, appName);
            }
        };

        m_task = new SeqRunTask(new StopSyncAppTask(), new StoreLastSyncRunAppTask(workDir),
            redisStartbgTask, rhoconnectStartdebugTask);
        m_debugProcess = null;
    }

    @Override
    public boolean isOk()
    {
        return m_task.isOk();
    }

    @Override
    public void run(IProgressMonitor monitor)
    {
        m_task.run(monitor);
    }

    public IProcess getDebugProcess()
    {
        return m_debugProcess;
    }
}
