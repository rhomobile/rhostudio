package rhogenwizard.sdk.task.run;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyDebugTask;
import rhogenwizard.sdk.task.RubyExecTask;
import rhogenwizard.sdk.task.RunTask;
import rhogenwizard.sdk.task.SeqRunTask;
import rhogenwizard.sdk.task.StopSyncAppTask;
import rhogenwizard.sdk.task.StoreLastSyncRunAppTask;

public class RunDebugRhoconnectAppTask extends RunTask
{
    private final RubyDebugTask m_rhoconnectStartdebugTask;
    private final RunTask       m_task;

    public RunDebugRhoconnectAppTask(String workDir, String appName, ILaunch launch)
    {
        RunTask redisStartbgTask = new RubyExecTask(workDir, SysCommandExecutor.RUBY_BAT, "rake",
            "redis:startbg");

        m_rhoconnectStartdebugTask = new RubyDebugTask(launch, appName, workDir, SysCommandExecutor.RUBY_BAT,
            "rake", "rhoconnect:startdebug");

        m_task = new SeqRunTask(new StopSyncAppTask(), new StoreLastSyncRunAppTask(workDir),
            redisStartbgTask, m_rhoconnectStartdebugTask);
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
        return m_rhoconnectStartdebugTask.getDebugProcess();
    }
}
