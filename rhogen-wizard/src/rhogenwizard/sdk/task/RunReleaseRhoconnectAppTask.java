package rhogenwizard.sdk.task;

public class RunReleaseRhoconnectAppTask extends SeqRunTask
{
    private static RunTask[] getTasks(final String workDir)
    {
        RunTask redisStartbgTask = new ARubyTask(workDir, "rake", "redis:startbg");
        RunTask rhoconnectStartbgTask = new ARubyTask(workDir, "rake", "rhoconnect:startbg");

        return new RunTask[] { new StopSyncAppTask(), new StoreLastSyncRunAppTask(workDir), redisStartbgTask,
            rhoconnectStartbgTask };
    }

    public RunReleaseRhoconnectAppTask(String workDir)
    {
        super(getTasks(workDir));
    }
}
