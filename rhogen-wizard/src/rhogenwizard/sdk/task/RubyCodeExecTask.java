package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

public class RubyCodeExecTask extends RubyExecTask
{
    public RubyCodeExecTask(String... codeLines)
    {
        super("c:\\Android", "ruby", getArgs(codeLines));
        
        disableConsole();
    }

    private static String[] getArgs(String[] codeLines)
    {
        List<String> args = new ArrayList<String>();
        
        for (String codeLine : codeLines)
        {
            codeLine = codeLine.replaceAll("\\\"", "\\\\\"");
            
            args.add("-e");
            args.add("\"" + codeLine + "\"");
        }
        
        return args.toArray(new String[0]);
    }
}
