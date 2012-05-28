package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;

import rhogenwizard.Activator;
import rhogenwizard.ConsoleHelper;
import rhogenwizard.OSValidator;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.constants.ConfigurationConstants;
import rhogenwizard.sdk.helper.ConsoleAppAdapter;

public class RunReleaseRhoconnectAppTask extends SeqRunTask
{
    private static RunTask[] getTasks(final String workDir_)
    {
        RunTask storeLastSyncRunAppTask = new RunTask()
        {
            @Override
            public void setData(Map<String, ?> data)
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public Map<String, ?> getResult()
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public void run(IProgressMonitor monitor)
            {
                IPreferenceStore store = Activator.getDefault().getPreferenceStore();
                store.setValue(ConfigurationConstants.lastSyncRunApp, workDir_);
            }
        };

        RunTask redisStartbgTask = new RakeTask()
        {
            @Override
            protected void exec()
            {
                List<String> cmdLine = Arrays.asList(m_rakeExe, "redis:startbg");

                try
                {
                    m_executor.setWorkingDirectory(workDir_);
                    m_executor.runCommand(cmdLine);
                }
                catch (Exception e)
                {
                }
            }
        };

        RunTask rhoconnectStartTask = new RunTask()
        {
            @Override
            public void setData(Map<String, ?> data)
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public Map<String, ?> getResult()
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public void run(IProgressMonitor monitor)
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        String rakeExe = "rake";
                        if (OSValidator.OSType.WINDOWS == OSValidator.detect())
                        {
                            rakeExe = rakeExe + ".bat";
                        }

                        SysCommandExecutor executor = new SysCommandExecutor();

                        executor.setOutputLogDevice(new ConsoleAppAdapter());
                        executor.setErrorLogDevice(new ConsoleAppAdapter());

                        if (workDir_ == null)
                            return;

                        ConsoleHelper.showAppConsole();

                        List<String> cmdLine = Arrays.asList(rakeExe, "rhoconnect:start");

                        try
                        {
                            executor.setWorkingDirectory(workDir_);
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

        return new RunTask[] { new StopSyncAppTask(), storeLastSyncRunAppTask, redisStartbgTask,
            rhoconnectStartTask };
    }

    public RunReleaseRhoconnectAppTask(String workDir)
    {
        super(getTasks(workDir));
    }
}
