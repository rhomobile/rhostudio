package rhogenwizard.sdk.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import rhogenwizard.Activator;
import rhogenwizard.constants.ConfigurationConstants;

public abstract class RunRhoconnectAppTask extends RhoconnectTask
{
    public void stopSyncApp() throws Exception
    {
        if (Activator.getDefault() == null)
            return;

        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        if (store == null)
            return;

        String prevRunningRhoconnectApp = store.getString(ConfigurationConstants.lastSyncRunApp);

        if (prevRunningRhoconnectApp == null || prevRunningRhoconnectApp.length() == 0)
            return;

        File appFolder = new File(prevRunningRhoconnectApp);

        if (!appFolder.exists())
            return;

        StringBuilder sb = new StringBuilder();
        sb.append("rhoconnect:stop");

        m_executor.setWorkingDirectory(prevRunningRhoconnectApp);

        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add(m_rakeExe);
        cmdLine.add(sb.toString());

        m_executor.runCommand(cmdLine);

        sb = new StringBuilder();
        sb.append("redis:stop");

        m_executor.setWorkingDirectory(prevRunningRhoconnectApp);

        cmdLine.clear();
        cmdLine.add(m_rakeExe);
        cmdLine.add(sb.toString());

        m_executor.runCommand(cmdLine);
    }
}
