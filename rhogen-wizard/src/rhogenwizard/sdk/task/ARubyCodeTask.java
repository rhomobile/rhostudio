package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

public class ARubyCodeTask extends ARubyTask
{
    public ARubyCodeTask(String... codeLines)
    {
        super(null, "ruby", getArgs(codeLines));
        disableConsole();
    }

    private static String[] getArgs(String[] codeLines)
    {
        List<String> args = new ArrayList<String>();
        for (String codeLine : codeLines)
        {
            args.add("-e");
            args.add(escape(codeLine));
        }
        return args.toArray(new String[0]);
    }

    private static String escape(String codeLine)
    {
        return codeLine;
    }
}
