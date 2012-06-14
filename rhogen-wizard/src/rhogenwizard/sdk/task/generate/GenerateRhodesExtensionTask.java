package rhogenwizard.sdk.task.generate;

import rhogenwizard.sdk.task.RubyExecTask;

public class GenerateRhodesExtensionTask extends RubyExecTask
{
    public GenerateRhodesExtensionTask(String workDir, String extName)
    {
        super(workDir, "rhodes", "extension", extName);
    }
}
