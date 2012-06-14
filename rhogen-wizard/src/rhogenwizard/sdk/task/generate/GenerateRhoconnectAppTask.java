package rhogenwizard.sdk.task.generate;

import rhogenwizard.sdk.task.RubyExecTask;

public class GenerateRhoconnectAppTask extends RubyExecTask
{
    public GenerateRhoconnectAppTask(String workDir, String appName)
    {
        super(workDir, "rhoconnect", "app", appName);
    }
}
