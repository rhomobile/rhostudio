package rhogenwizard.sdk.task;

import java.io.File;

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

        RunTask stopRhoconnectTask = new ARakeTask(prevRunningRhoconnectApp, "rhoconnect:stop");
        RunTask stopRedisTask = new ARakeTask(prevRunningRhoconnectApp, "redis:stop");
        return new RunTask[] { stopRhoconnectTask, stopRedisTask };
    }

    public StopSyncAppTask()
    {
        super(getTasks());
    }
}
