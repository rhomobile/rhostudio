package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

import rhogenwizard.SysCommandExecutor;

public class RubyCodeExecTask extends RubyExecTask
{
    public RubyCodeExecTask(String... codeLines)
    {
        super(null, SysCommandExecutor.RUBY, getArgs(codeLines));

        disableConsole();
    }

    private static String[] getArgs(String[] codeLines)
    {
        List<String> args = new ArrayList<String>();

        args.add("ruby");

        for (String codeLine : codeLines)
        {
            args.add("-e");
            args.add(codeLine);
        }

        return args.toArray(new String[0]);
    }
}
