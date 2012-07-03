package rhogenwizard.sdk.task.generate;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;

public class GenerateRhodesAppTask extends RubyExecTask
{
    public GenerateRhodesAppTask(String workDir, String appName)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, "rhodes", "app", appName);
    }
}
