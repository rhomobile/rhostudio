package rhogenwizard.sdk.task;

public class GenerateRhoconnectAdapterTask extends ARubyTask
{
    public GenerateRhoconnectAdapterTask(String workDir, String sourceName)
    {
        super(workDir, "rhoconnect", "source", sourceName);
    }
}
