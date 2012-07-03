package rhogenwizard.sdk.task;

import rhogenwizard.PlatformType;
import rhogenwizard.SysCommandExecutor;

public class BuildPlatformTask extends RubyExecTask
{
    public BuildPlatformTask(String workDir, PlatformType platformType)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, "rake", "device:" + platformType + ":production");
    }
}
