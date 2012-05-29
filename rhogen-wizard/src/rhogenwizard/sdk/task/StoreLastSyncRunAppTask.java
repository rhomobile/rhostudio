package rhogenwizard.sdk.task;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;

import rhogenwizard.Activator;
import rhogenwizard.constants.ConfigurationConstants;

public class StoreLastSyncRunAppTask extends RunTask
{
    private final String m_workDir;

    public StoreLastSyncRunAppTask(String workDir)
    {
        m_workDir = workDir;
    }

    @Override
    public boolean isOk()
    {
        return true;
    }

    @Override
    public void run(IProgressMonitor monitor)
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setValue(ConfigurationConstants.lastSyncRunApp, m_workDir);
    }
}
