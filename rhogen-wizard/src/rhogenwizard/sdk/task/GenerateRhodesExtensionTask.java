package rhogenwizard.sdk.task;

public class GenerateRhodesExtensionTask extends RubyExecTask
{
    public GenerateRhodesExtensionTask(String workDir, String extName)
    {
        super(workDir, "rhodes", "extension", extName);
    }
}
