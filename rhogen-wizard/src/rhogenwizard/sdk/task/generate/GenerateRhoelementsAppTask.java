package rhogenwizard.sdk.task.generate;

import rhogenwizard.sdk.task.RubyExecTask;

public class GenerateRhoelementsAppTask extends RubyExecTask
{
    public GenerateRhoelementsAppTask(String workDir, String appName)
    {
        super(workDir, "rhoelements", "app", appName);
    }
}
