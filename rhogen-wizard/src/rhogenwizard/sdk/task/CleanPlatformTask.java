package rhogenwizard.sdk.task;

import rhogenwizard.PlatformType;

public class CleanPlatformTask extends ARakeTask
{
    public CleanPlatformTask(String workDir, PlatformType platformType)
    {
        super(workDir, "clean:" + platformType);
    }
}
