package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.ILaunch;

import rhogenwizard.PlatformType;

public class RunDebugRhodesAppTask extends RubyDebugTask
{
    public RunDebugRhodesAppTask(ILaunch launch, String workDir, String appName, PlatformType platformType,
        boolean isReloadCode, boolean isTrace)
    {
        super(launch, appName, workDir, getArgs(platformType, isTrace, isReloadCode));
    }

    private static String[] getArgs(PlatformType platformType, boolean isReloadCode, boolean isTrace)
    {
        List<String> args = new ArrayList<String>();
        args.add("rake");
        args.add("run:" + platformType + ":rhosimulator_debug");

        if (isTrace)
        {
            args.add("--trace");
        }

        args.add("rho_debug_port=9000");
        args.add("rho_reload_app_changes=" + (isReloadCode ? "1" : "0"));

        return args.toArray(new String[0]);
    }
}
