package rhogenwizard.sdk.task.run;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;
import rhogenwizard.sdk.task.RunTask;
import rhogenwizard.sdk.task.SeqRunTask;
import rhogenwizard.sdk.task.StopSyncAppTask;
import rhogenwizard.sdk.task.StoreLastSyncRunAppTask;

public class RunReleaseRhoconnectAppTask extends SeqRunTask
{
    private static RunTask[] getTasks(final String workDir)
    {
        RunTask redisStartbgTask = new RubyExecTask(workDir, SysCommandExecutor.RUBY_BAT, "rhoconnect",
            "redis-startbg");
        RunTask rhoconnectStartbgTask = new RubyExecTask(workDir, SysCommandExecutor.RUBY_BAT, "rhoconnect",
            "startbg");

        return new RunTask[] { new StopSyncAppTask(), new StoreLastSyncRunAppTask(workDir), redisStartbgTask,
            rhoconnectStartbgTask };
    }

    public RunReleaseRhoconnectAppTask(String workDir)
    {
        super(getTasks(workDir));
    }
}
