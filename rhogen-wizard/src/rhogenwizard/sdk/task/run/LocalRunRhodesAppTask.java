package rhogenwizard.sdk.task.run;

import java.util.ArrayList;
import java.util.List;

import rhogenwizard.PlatformType;
import rhogenwizard.RunType;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;

public class LocalRunRhodesAppTask extends RubyExecTask
{
    private static String[] getArgs(PlatformType platformType, RunType runType,
        boolean isReloadCode, boolean isTrace, String startPathOverride,
        String[] additionalRubyExtensions)
    {
        String task;
        if (runType == RunType.eDevice)
            switch (platformType) {
            case eIPhone:
            default:
                task = "run:" + platformType.id + ":device";
                break;
            }
        else if (runType == RunType.eRhoSimulator)
            task = "run:" + platformType.id + ":rhosimulator";
        else
            task = "run:" + platformType.id;

        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add("rake");
        cmdLine.add(task);

        if (isTrace)
        {
            cmdLine.add("--trace");
        }

        if (runType == RunType.eRhoSimulator)
        {
            cmdLine.add("rho_debug_port=9000");
            cmdLine.add("rho_reload_app_changes=" + (isReloadCode ? "1" : "0"));
        }

        if (startPathOverride != null)
        {
            cmdLine.add("rho_override_start_path=\'" + startPathOverride + "\'");
        }

        if (additionalRubyExtensions != null && additionalRubyExtensions.length > 0)
        {
            cmdLine.add("rho_extensions=" + join(",", additionalRubyExtensions));
        }

        return cmdLine.toArray(new String[0]);
    }

    public LocalRunRhodesAppTask(String workDir, PlatformType platformType, RunType runType,
        boolean isReloadCode, boolean isTrace, String startPathOverride,
        String[] additionalRubyExtensions)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, getArgs(platformType, runType, isReloadCode,
            isTrace, startPathOverride, additionalRubyExtensions));
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
