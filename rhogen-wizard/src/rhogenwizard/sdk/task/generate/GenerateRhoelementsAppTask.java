package rhogenwizard.sdk.task.generate;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;

public class GenerateRhoelementsAppTask extends RubyExecTask
{
    public GenerateRhoelementsAppTask(String workDir, String appName)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, "rhoelements", "app", appName);
    }
}
