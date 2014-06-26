package rhogenwizard;

import org.eclipse.jface.preference.IPreferenceStore;

public class RhodesStore
{
    private static final String lastSyncRunApp = "sync_app";
    
    private final IPreferenceStore store;

    public RhodesStore(IPreferenceStore store)
    {
        this.store = store;
    }

    public String lastSyncRunApp()
    {
        return store.getString(RhodesStore.lastSyncRunApp);
    }

    public void lastSyncRunApp(String v)
    {
        store.setValue(RhodesStore.lastSyncRunApp, v);
    }
}
