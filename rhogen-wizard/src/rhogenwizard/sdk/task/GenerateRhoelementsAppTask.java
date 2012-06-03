package rhogenwizard.sdk.task;

public class GenerateRhoelementsAppTask extends RubyExecTask
{
    public GenerateRhoelementsAppTask(String workDir, String appName)
    {
        super(workDir, "rhoelements", "app", appName);
    }
}
