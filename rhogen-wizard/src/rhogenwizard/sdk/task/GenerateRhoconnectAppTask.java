package rhogenwizard.sdk.task;

public class GenerateRhoconnectAppTask extends RubyExecTask
{
    public GenerateRhoconnectAppTask(String workDir, String appName)
    {
        super(workDir, "rhoconnect", "app", appName);
    }
}
