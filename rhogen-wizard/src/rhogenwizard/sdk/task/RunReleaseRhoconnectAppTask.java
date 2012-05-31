package rhogenwizard.sdk.task;

import org.eclipse.core.runtime.IProgressMonitor;

import rhogenwizard.ConsoleHelper;

public class RunReleaseRhoconnectAppTask extends SeqRunTask
{
    private static RunTask[] getTasks(final String workDir)
    {
        RunTask redisStartbgTask = new ARubyTask(workDir, "rake", "redis:startbg");

        RunTask rhoconnectStartTask = new RunTask()
        {
            @Override
            public boolean isOk()
            {
                return true;
            }

            @Override
            public void run(IProgressMonitor monitor)
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ConsoleHelper.showAppConsole();
                        new ARubyTask(workDir, "rake", "rhoconnect:start").run();
                    }
                }).start();
            }
        };

        return new RunTask[] { new StopSyncAppTask(), new StoreLastSyncRunAppTask(workDir), redisStartbgTask,
            rhoconnectStartTask };
    }

    public RunReleaseRhoconnectAppTask(String workDir)
    {
        super(getTasks(workDir));
    }
}
