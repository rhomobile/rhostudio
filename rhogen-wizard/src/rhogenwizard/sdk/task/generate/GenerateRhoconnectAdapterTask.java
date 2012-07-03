package rhogenwizard.sdk.task.generate;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;

public class GenerateRhoconnectAdapterTask extends RubyExecTask
{
    public GenerateRhoconnectAdapterTask(String workDir, String sourceName)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, "rhoconnect", "source", sourceName);
    }
}
