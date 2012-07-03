package rhogenwizard.sdk.task.generate;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;

public class GenerateRhodesSpecTask extends RubyExecTask
{
    public GenerateRhodesSpecTask(String workDir)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, "rhodes", "spec");
    }
}
