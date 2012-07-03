package rhogenwizard.sdk.task.generate;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;

public class GenerateRhodesExtensionTask extends RubyExecTask
{
    public GenerateRhodesExtensionTask(String workDir, String extName)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, "rhodes", "extension", extName);
    }
}
