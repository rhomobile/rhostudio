package rhogenwizard.sdk.task;

public class GenerateRhodesAppTask extends ARubyTask
{
    public GenerateRhodesAppTask(String workDir, String appName)
    {
        super(workDir, "rhodes", "app", appName);
    }
}
