package rhogenwizard.sdk.task;

import rhogenwizard.PlatformType;
import rhogenwizard.SysCommandExecutor;

public class RhohubProductionBuildTask extends RubyExecTask
{
    public RhohubProductionBuildTask(String workDir, PlatformType platformType)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, "rake", "rhohub:build:" + platformType + ":production");
    }
}
