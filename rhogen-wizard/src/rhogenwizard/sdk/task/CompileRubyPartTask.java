package rhogenwizard.sdk.task;

import java.util.Arrays;
import java.util.List;

public class CompileRubyPartTask extends RubyExecTask
{
    public CompileRubyPartTask(String workDir)
    {
        super(workDir, "rake", "build:bundle:rhostudio");
        disableConsole();
    }

    public List<String> getOutputStrings()
    {
        return Arrays.asList(getOutput().split("[\n\r]+"));
    }
}
