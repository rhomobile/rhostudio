package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.helper.ConsoleAppAdapter;

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
                        SysCommandExecutor executor = new SysCommandExecutor();

                        executor.setOutputLogDevice(new ConsoleAppAdapter());
                        executor.setErrorLogDevice(new ConsoleAppAdapter());

                        if (workDir == null)
                            return;

                        ConsoleHelper.showAppConsole();

                        List<String> cmdLine = Arrays.asList(RubyTask.getCommand("rake"), "rhoconnect:start");

                        try
                        {
                            executor.setWorkingDirectory(workDir);
                            executor.runCommand(cmdLine);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
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
