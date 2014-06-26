package rhogenwizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;


public class RhodesConfigurationRO
{
    protected static final String projectAttribute = "project_name";
    protected static final String platformTypeAttribute = "platform";
    protected static final String runTypeAttribute = "type_symulator";
    protected static final String buildTypeAttribute = "build";
    protected static final String androidVersionAttribute = "aversion";
    protected static final String iphoneVersionAttribute = "ipversion";
    protected static final String androidEmuNameAttribute = "aemuname";

    protected static final String cleanAttribute = "clean";
    protected static final String reloadCodeAttribute = "rebuild";
    protected static final String traceAttribute = "trace";

    private final ILaunchConfiguration configuration;
    
    public RhodesConfigurationRO(ILaunchConfiguration configuration)
    {
        this.configuration = configuration;
    }

    public String project()
    {
        return getString(projectAttribute, "");
    }
    
    public PlatformType platformType()
    {
        return PlatformType.fromId(getString(platformTypeAttribute, null));
    }

    public RunType runType()
    {
        return RunType.fromId(getString(runTypeAttribute, null));
    }

    public BuildType buildType()
    {
        return BuildType.fromId(getString(buildTypeAttribute, null));
    }

    public String androidVersion()
    {
        return getString(androidVersionAttribute, "");
    }
    
    public String iphoneVersion()
    {
        return getString(iphoneVersionAttribute, "");
    }
    
    public String androidEmulator()
    {
        return getString(androidEmuNameAttribute, "");
    }
    
    public boolean clean()
    {
        return getBoolean(cleanAttribute, false);
    }

    public boolean reloadCode()
    {
        return getBoolean(reloadCodeAttribute, false);
    }

    public boolean trace()
    {
        return getBoolean(traceAttribute, false);
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