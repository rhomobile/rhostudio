package rhogenwizard.rhohub;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;


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
        return m_projectSetting.get(rhoHubToken, "");
    }

    @Override
    public String getServerUrl()
    {
        return m_projectSetting.get(rhoHubUrl, "");
    }

    @Override
    public String getSelectedPlatform()
    {
        return m_projectSetting.get(rhoHubSelectedPlatform, "");
    }

    @Override
    public String getRhodesBranch()
    {
        return m_projectSetting.get(rhoHubSelectedRhodesVesion, "");
    }

    @Override
    public String getAppBranch()
    {
        return "master"; //projectSetting.getString(rhoHubUrl);
    }

    @Override
    public void setToken(String value) throws BackingStoreException
    {
        m_projectSetting.put(rhoHubToken, value);
        m_projectSetting.flush();
    }
    
    @Override
    public void setServerUrl(String value) throws BackingStoreException
    {
        m_projectSetting.put(rhoHubUrl, value);
        m_projectSetting.flush();
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
        m_projectSetting.put(rhoHubSelectedRhodesVesion, value);
        m_projectSetting.flush();
    }

    @Override
    public void setAppBranch(String value)
    {
        //projectSetting.put(, value);
    }

    @Override
    public void setLinking() throws BackingStoreException
    {
        m_projectSetting.put(isRhoHubLink, "");   
        m_projectSetting.flush();
    }
}
