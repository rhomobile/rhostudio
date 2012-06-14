package rhogenwizard.sdk.task.generate;

import rhogenwizard.sdk.task.RubyExecTask;

public class GenerateRhoconnectAdapterTask extends RubyExecTask
{
    public GenerateRhoconnectAdapterTask(String workDir, String sourceName)
    {
        super(workDir, "rhoconnect", "source", sourceName);
    }
}
