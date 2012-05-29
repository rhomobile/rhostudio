package rhogenwizard.sdk.task;

public class GenerateRhoelementsAppTask extends ARubyTask
{
    public GenerateRhoelementsAppTask(String workDir, String appName)
    {
        super(workDir, "rhoelements", "app", appName);
    }
}
