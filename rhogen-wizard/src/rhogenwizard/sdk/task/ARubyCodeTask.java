package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

import rhogenwizard.ILogDevice;

public class ARubyCodeTask extends ARubyTask
{
    private static ILogDevice nullLogDevice = new ILogDevice()
                                            {
                                                @Override
                                                public void log(String str)
                                                {
                                                }
                                            };

    public ARubyCodeTask(String... codeLines)
    {
        super(null, "ruby", getArgs(codeLines));
        m_executor.setOutputLogDevice(nullLogDevice);
        m_executor.setErrorLogDevice(nullLogDevice);
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
