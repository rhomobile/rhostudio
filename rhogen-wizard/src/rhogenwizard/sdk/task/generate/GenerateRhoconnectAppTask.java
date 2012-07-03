package rhogenwizard.sdk.task.generate;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;

public class GenerateRhoconnectAppTask extends RubyExecTask
{
    public GenerateRhoconnectAppTask(String workDir, String appName)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, "rhoconnect", "app", appName);
    }
}
