package rhogenwizard.sdk.task;

import rhogenwizard.PlatformType;
import rhogenwizard.SysCommandExecutor;

public class RhohubProductionBuildTask extends SeqRunTask
{
    public RhohubProductionBuildTask(String workDir, PlatformType platformType)
    {
        super(
            new RubyExecTask(workDir, SysCommandExecutor.RUBY_BAT,
                "rake", "cloud:build:" + platformType + ":production", "--trace"
            ),
            new RubyExecTask(workDir, SysCommandExecutor.RUBY_BAT,
                "rake", "cloud:download", "--trace"
            )
        );
    }
}
