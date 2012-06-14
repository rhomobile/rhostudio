package rhogenwizard.sdk.task.generate;

import rhogenwizard.sdk.task.RubyExecTask;

public class GenerateRhodesAppTask extends RubyExecTask
{
    public GenerateRhodesAppTask(String workDir, String appName)
    {
        super(workDir, "rhodes", "app", appName);
    }
}
