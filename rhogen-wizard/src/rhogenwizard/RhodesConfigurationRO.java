package rhogenwizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import rhogenwizard.constants.ConfigurationConstants;

public class RhodesConfigurationRO
{
    private final ILaunchConfiguration configuration;
    
    public RhodesConfigurationRO(ILaunchConfiguration configuration)
    {
        this.configuration = configuration;
    }

    public String project()
    {
        return getString(ConfigurationConstants.projectNameCfgAttribute, "");
    }
    
    public PlatformType platformType()
    {
        return PlatformType.fromId(getString(ConfigurationConstants.platformCfgAttribute, null));
    }

    public RunType runType()
    {
        return RunType.fromId(getString(ConfigurationConstants.simulatorType, null));
    }

    public BuildType buildType()
    {
        return BuildType.fromId(getString(ConfigurationConstants.buildCfgAttribute, null));
    }

    public String androidVersion()
    {
        return getString(ConfigurationConstants.androidVersionAttribute, "");
    }
    
    public String iphoneVersion()
    {
        return getString(ConfigurationConstants.iphoneVersionAttribute, "");
    }
    
    public String androidEmulator()
    {
        return getString(ConfigurationConstants.androidEmuNameAttribute, "");
    }
    
    public boolean clean()
    {
        return getBoolean(ConfigurationConstants.isCleanAttribute, false);
    }

    public boolean reloadCode()
    {
        return getBoolean(ConfigurationConstants.isReloadCodeAttribute, false);
    }

    public boolean trace()
    {
        return getBoolean(ConfigurationConstants.isTraceAttribute, false);
    }

    
    private boolean getBoolean(String attributeName, boolean defaultValue)
    {
        try
        {
            return configuration.getAttribute(attributeName, defaultValue);
        }
        catch (CoreException e)
        {
            return defaultValue;
        }
    }

    private String getString(String attributeName, String defaultValue)
    {
        try
        {
            return configuration.getAttribute(attributeName, defaultValue);
        }
        catch (CoreException e)
        {
            return defaultValue;
        }
    }
}