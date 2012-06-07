package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

import rhogenwizard.PlatformType;
import rhogenwizard.RunType;

public class RunReleaseRhodesAppTask extends RubyExecTask
{
    private static String[] getArgs(PlatformType platformType, RunType runType, boolean isReloadCode,
        boolean isTrace)
    {
        String task;
        if (runType == RunType.eDevice)
            if (platformType == PlatformType.eIPhone)
                task = "device:iphone:production";
            else if (platformType == PlatformType.eBb) // FIX for bb
                task = "device:bb:production";
            else
                task = "run:" + platformType + ":device";
        else if (runType == RunType.eRhoEmulator)
            task = "run:" + platformType + ":rhosimulator";
        else
            task = "run:" + platformType;

        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add("rake");
        cmdLine.add(task);

        if (isTrace)
        {
            cmdLine.add("--trace");
        }

        if (runType == RunType.eRhoEmulator)
        {
            cmdLine.add("rho_debug_port=9000");
            cmdLine.add("rho_reload_app_changes=" + (isReloadCode ? "1" : "0"));
        }

        return cmdLine.toArray(new String[0]);
    }

    public RunReleaseRhodesAppTask(String workDir, PlatformType platformType, RunType runType,
        boolean isReloadCode, boolean isTrace)
    {
        super(workDir, getArgs(platformType, runType, isReloadCode, isTrace));
    }
}
