package rhogenwizard;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import rhogenwizard.constants.ConfigurationConstants;

public class RhodesConfigurationRW extends RhodesConfigurationRO
{
    private final ILaunchConfigurationWorkingCopy configuration;
    
    public RhodesConfigurationRW(ILaunchConfigurationWorkingCopy configuration)
    {
        super(configuration);
        this.configuration = configuration;
    }

    public void project(String p)
    {
        configuration.setAttribute(ConfigurationConstants.projectNameCfgAttribute, p);
    }

    public void platformType(PlatformType pt)
    {
        configuration.setAttribute(ConfigurationConstants.platformCfgAttribute, pt.id);
    }

    public void runType(RunType rt)
    {
        configuration.setAttribute(ConfigurationConstants.simulatorType, rt.id);
    }

    public void buildType(BuildType bt)
    {
        configuration.setAttribute(ConfigurationConstants.buildCfgAttribute, bt.id);
    }
    
    public void androidVersion(String v)
    {
        configuration.setAttribute(ConfigurationConstants.androidVersionAttribute, v);
    }
    
    public void iphoneVersion(String v)
    {
        configuration.setAttribute(ConfigurationConstants.iphoneVersionAttribute, v);
    }
    
    public void androidEmulator(String v)
    {
        configuration.setAttribute(ConfigurationConstants.androidEmuNameAttribute, v);
    }
    
    public void clean(boolean f)
    {
        configuration.setAttribute(ConfigurationConstants.isCleanAttribute, f);
    }

    public void reloadCode(boolean f)
    {
        configuration.setAttribute(ConfigurationConstants.isReloadCodeAttribute, f);
    }

    public void trace(boolean f)
    {
        configuration.setAttribute(ConfigurationConstants.isTraceAttribute, f);
    }
}