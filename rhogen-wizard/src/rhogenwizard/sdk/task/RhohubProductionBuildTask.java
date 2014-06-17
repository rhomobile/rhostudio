package rhogenwizard.sdk.task;

import rhogenwizard.CloudUtils;
import rhogenwizard.PlatformType;
import rhogenwizard.SysCommandExecutor;

public class RhohubProductionBuildTask extends SeqRunTask
{
    public RhohubProductionBuildTask(String workDir, PlatformType platformType)
    {
        super(
            new RubyExecTask(workDir, SysCommandExecutor.RUBY_BAT,
                "rake", CloudUtils.buildTask(platformType), "--trace"
            ),
            new RubyExecTask(workDir, SysCommandExecutor.RUBY_BAT,
                "rake", "cloud:download", "--trace"
            )
        );
    }
}
