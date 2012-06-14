package rhogenwizard.sdk.task.generate;

import rhogenwizard.sdk.task.RubyExecTask;

public class GenerateRhodesSpecTask extends RubyExecTask
{
    public GenerateRhodesSpecTask(String workDir)
    {
        super(workDir, "rhodes", "spec");
    }
}
