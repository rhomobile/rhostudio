package rhogenwizard.rhohub;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

import rhogenwizard.Activator;
import rhogenwizard.preferences.PreferenceInitializer;


public class RhoHubBundleSetting implements IRhoHubSetting, IRhoHubSettingSetter
{
    private static String rhohubTag = "rhohub";
    
    private IEclipsePreferences m_projectSetting = null;

    public static IRhoHubSetting createGetter(IProject project)
    {
        return new RhoHubBundleSetting(project);
    }

    public static IRhoHubSettingSetter createSetter(IProject project)
    {
        return new RhoHubBundleSetting(project);
    }
    
    public RhoHubBundleSetting(IProject project)
    {
        IScopeContext projectScope = new ProjectScope(project);
        m_projectSetting = projectScope.getNode(rhohubTag);
    }

    @Override
    public boolean isLinking()
    {
        return m_projectSetting.get(isRhoHubLink, null) == null ? false : true;
    }

    @Override
    public String getToken()
    {
        return RhoHubCommands.getToken(PreferenceInitializer.getRhodesPath());
    }

    @Override
    public String getServerUrl()
    {
        throw new UnsupportedOperationException(
            "TODO: add getServerUrl implementation. By rake command for example.");
    }

    @Override
    public String getSelectedPlatform()
    {
        return m_projectSetting.get(rhoHubSelectedPlatform, "");
    }

    @Override
    public String getRhodesBranch()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        if (store == null)
            return "";
        
        String selVer = store.getString(rhoHubSelectedRhodesVesion);
        
        return selVer == "" ? "3.3.2" : selVer;
    }

    @Override
    public String getAppBranch()
    {
        return "master";
    }

    @Override
    public void setSelectedPlatform(String value) throws BackingStoreException
    {
        m_projectSetting.put(rhoHubSelectedPlatform, value);
        m_projectSetting.flush();
    }

    @Override
    public void setRhodesBranch(String value) throws BackingStoreException
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        
        if (store == null)
            return;
        
        store.setValue(rhoHubSelectedRhodesVesion, value);
    }

    @Override
    public void setAppBranch(String value)
    {
    }

    @Override
    public void setLinking() throws BackingStoreException
    {
        m_projectSetting.put(isRhoHubLink, "");   
        m_projectSetting.flush();
    }

	@Override
	public void unsetLinking() throws BackingStoreException
	{
        m_projectSetting.remove(isRhoHubLink);   
        m_projectSetting.flush();		
	}

	@Override
	public String getHttpProxy() 
	{
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        if (store == null)
            return "";
        
        return store.getString(rhoHubProxy);
	}
}
