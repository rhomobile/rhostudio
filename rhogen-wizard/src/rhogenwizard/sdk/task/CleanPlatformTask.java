package rhogenwizard.sdk.task;

import rhogenwizard.PlatformType;
import rhogenwizard.SysCommandExecutor;

public class CleanPlatformTask extends RubyExecTask
{
    public CleanPlatformTask(String workDir, PlatformType platformType)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, "rake", "clean:" + platformType.id);
    }
}
