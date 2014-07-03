package rhogenwizard;

import org.eclipse.jface.preference.IPreferenceStore;

public class RhodesStore
{
    private static final String lastSyncRunApp = "sync_app";
    
    private static final String productionBuildProjectName = "production_build_project_name";
    private static final String productionBuildPlatform    = "production_build_platform";
    private static final String productionBuildBuild       = "production_build_build";

    private final IPreferenceStore store;

    public RhodesStore(IPreferenceStore store)
    {
        this.store = store;
    }

    public String lastSyncRunApp()
    {
        return store.getString(lastSyncRunApp);
    }

    public void lastSyncRunApp(String v)
    {
        store.setValue(lastSyncRunApp, v);
    }

    public String productionBuildProjectName()
    {
        return store.getString(productionBuildProjectName);
    }

    public void productionBuildProjectName(String n)
    {
        store.setValue(productionBuildProjectName, n);
    }

    public PlatformType productionBuildPlatform()
    {
        return PlatformType.fromId(store.getString(productionBuildPlatform));
    }

    public void productionBuildPlatform(PlatformType pt)
    {
        store.setValue(productionBuildPlatform, pt.id);
    }

    public BuildType productionBuildBuild()
    {
        return BuildType.fromId(store.getString(productionBuildBuild));
    }

    public void productionBuildBuild(BuildType bt)
    {
        store.setValue(productionBuildBuild, bt.id);
    }
}
