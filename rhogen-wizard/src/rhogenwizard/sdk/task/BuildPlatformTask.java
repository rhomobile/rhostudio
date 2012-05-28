package rhogenwizard.sdk.task;

import rhogenwizard.PlatformType;

public class BuildPlatformTask extends ARakeTask
{
    public BuildPlatformTask(String workDir, PlatformType platformType)
    {
        super(workDir, "device:" + platformType + ":production");
    }
}
