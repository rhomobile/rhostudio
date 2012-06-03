package rhogenwizard.sdk.task;

public class GenerateRhodesSpecTask extends RubyExecTask
{
    public GenerateRhodesSpecTask(String workDir)
    {
        super(workDir, "rhodes", "spec");
    }
}
