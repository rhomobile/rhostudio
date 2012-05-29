package rhogenwizard.sdk.task;

public class GenerateRhodesSpecTask extends ARubyTask
{
    public GenerateRhodesSpecTask(String workDir)
    {
        super(workDir, "rhodes", "spec");
    }
}
