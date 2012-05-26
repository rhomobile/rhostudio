package rhogenwizard.sdk.task;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import rhogenwizard.Activator;
import rhogenwizard.constants.ConfigurationConstants;

public class StopSyncAppTask extends SeqRunTask
{
    private static final RakeTask[] empty = {};

    private static RunTask[] getTasks()
    {
        if (Activator.getDefault() == null)
            return empty;

        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        if (store == null)
            return empty;

        final String prevRunningRhoconnectApp = store.getString(ConfigurationConstants.lastSyncRunApp);

        if (prevRunningRhoconnectApp == null || prevRunningRhoconnectApp.length() == 0)
            return empty;

        File appFolder = new File(prevRunningRhoconnectApp);

        if (!appFolder.exists())
            return empty;

        RunTask stopRhoconnectTask = new RakeTask()
        {
            @Override
            protected void exec()
            {
                List<String> cmdLine = Arrays.asList(m_rakeExe, "rhoconnect:stop");

                try
                {
                    m_executor.setWorkingDirectory(prevRunningRhoconnectApp);
                    m_executor.runCommand(cmdLine);
                }
                catch (Exception e)
                {
                }
            }
        };

        RunTask stopRedisTask = new RakeTask()
        {
            @Override
            protected void exec()
            {
                List<String> cmdLine = Arrays.asList(m_rakeExe, "redis:stop");

                try
                {
                    m_executor.setWorkingDirectory(prevRunningRhoconnectApp);
                    m_executor.runCommand(cmdLine);
                }
                catch (Exception e)
                {
                }
            }
        };

        return new RunTask[] { stopRhoconnectTask, stopRedisTask };
    }

    public StopSyncAppTask()
    {
        super(getTasks());
    }
}
