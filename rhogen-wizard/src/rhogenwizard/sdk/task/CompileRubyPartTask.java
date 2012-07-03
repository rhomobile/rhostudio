package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.List;

import rhogenwizard.SysCommandExecutor;

public class CompileRubyPartTask extends RubyExecTask
{
    public CompileRubyPartTask(String workDir)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, "rake", "build:bundle:rhostudio");
        disableConsole();
    }

    public List<String> getOutputStrings()
    {
        return Arrays.asList(getOutput().split("[\n\r]+"));
    }
}
