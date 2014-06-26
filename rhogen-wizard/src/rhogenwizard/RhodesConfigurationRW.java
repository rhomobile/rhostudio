package rhogenwizard;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;


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
        configuration.setAttribute(projectAttribute, p);
    }

    public void platformType(PlatformType pt)
    {
        configuration.setAttribute(platformTypeAttribute, pt.id);
    }

    public void runType(RunType rt)
    {
        configuration.setAttribute(runTypeAttribute, rt.id);
    }

    public void buildType(BuildType bt)
    {
        configuration.setAttribute(buildTypeAttribute, bt.id);
    }
    
    public void androidVersion(String v)
    {
        configuration.setAttribute(androidVersionAttribute, v);
    }
    
    public void iphoneVersion(String v)
    {
        configuration.setAttribute(iphoneVersionAttribute, v);
    }
    
    public void androidEmulator(String v)
    {
        configuration.setAttribute(androidEmuNameAttribute, v);
    }
    
    public void clean(boolean f)
    {
        configuration.setAttribute(cleanAttribute, f);
    }

    public void reloadCode(boolean f)
    {
        configuration.setAttribute(reloadCodeAttribute, f);
    }

    public void trace(boolean f)
    {
        configuration.setAttribute(traceAttribute, f);
    }
}