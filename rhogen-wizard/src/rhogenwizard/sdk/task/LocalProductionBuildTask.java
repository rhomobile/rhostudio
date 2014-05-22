package rhogenwizard.sdk.task;

import rhogenwizard.PlatformType;
import rhogenwizard.SysCommandExecutor;

public class LocalProductionBuildTask extends RubyExecTask
{
    public LocalProductionBuildTask(String workDir, PlatformType platformType)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, "rake", "device:" + platformType + ":production");
    }
}
