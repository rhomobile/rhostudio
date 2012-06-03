package rhogenwizard.sdk.task;

import rhogenwizard.PlatformType;

public class BuildPlatformTask extends RubyExecTask
{
    public BuildPlatformTask(String workDir, PlatformType platformType)
    {
        super(workDir, "rake", "device:" + platformType + ":production");
    }
}
