package rhogenwizard.sdk.task;

import rhogenwizard.PlatformType;

public class CleanPlatformTask extends ARubyTask
{
    public CleanPlatformTask(String workDir, PlatformType platformType)
    {
        super(workDir, "rake", "clean:" + platformType);
    }
}
