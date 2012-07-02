package rhogenwizard.sdk.task.run;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.ILaunch;

import rhogenwizard.PlatformType;
import rhogenwizard.WinMobileSdk;
import rhogenwizard.sdk.task.RubyDebugTask;

public class RunDebugRhodesAppTask extends RubyDebugTask
{
    public RunDebugRhodesAppTask(ILaunch launch, String workDir, String appName, PlatformType platformType,
        boolean isReloadCode, boolean isTrace, String startPathOverride, String wmSdkVersion,
        String[] additionalRubyExtensions)
    {
        super(launch, appName, workDir, getArgs(platformType, isTrace, isReloadCode, startPathOverride,
            wmSdkVersion, additionalRubyExtensions));
    }

    private static String[] getArgs(PlatformType platformType, boolean isReloadCode, boolean isTrace,
        String startPathOverride, String wmSdkVersion, String[] additionalRubyExtensions)
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

        if (startPathOverride != null)
        {
            args.add("rho_override_start_path=\'" + startPathOverride + "\'");
        }

        if (platformType == PlatformType.eWm && wmSdkVersion != null)
        {
            args.add("rho_wm_sdk=\'" + WinMobileSdk.fromVersion(wmSdkVersion).sdkId + "\'");
        }

        if (additionalRubyExtensions != null && additionalRubyExtensions.length > 0)
        {
            args.add("rho_extensions=" + join(",", additionalRubyExtensions));
        }

        return args.toArray(new String[0]);
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
