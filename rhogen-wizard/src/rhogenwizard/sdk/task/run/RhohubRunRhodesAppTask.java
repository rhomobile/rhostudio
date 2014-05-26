package rhogenwizard.sdk.task.run;

import java.util.ArrayList;
import java.util.List;

import rhogenwizard.PlatformType;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;
import rhogenwizard.sdk.task.SeqRunTask;

public class RhohubRunRhodesAppTask extends SeqRunTask
{
    public RhohubRunRhodesAppTask(String workDir, PlatformType platformType, boolean isTrace,
        String startPathOverride, String[] additionalRubyExtensions)
    {
        super(
            getTask(workDir, "rhohub:build:" + platformType + ":production", isTrace,
                startPathOverride, additionalRubyExtensions),
            getTask(workDir, "run:device", isTrace, startPathOverride, additionalRubyExtensions)
        );
    }
    
    private static RubyExecTask getTask(String workDir, String task, boolean isTrace,
        String startPathOverride, String[] additionalRubyExtensions)
    {
        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add("rake");
        cmdLine.add(task);

        if (isTrace)
        {
            cmdLine.add("--trace");
        }

        if (startPathOverride != null)
        {
            cmdLine.add("rho_override_start_path=\'" + startPathOverride + "\'");
        }

        if (additionalRubyExtensions != null && additionalRubyExtensions.length > 0)
        {
            cmdLine.add("rho_extensions=" + join(",", additionalRubyExtensions));
        }

        return new RubyExecTask(
            workDir, SysCommandExecutor.RUBY_BAT, cmdLine.toArray(new String[0]));
    }

    private static String join(String delimiter, String... text)
    {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String line : text)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                sb.append(delimiter);
            }
            sb.append(line);
        }
        return sb.toString();
    }
}
