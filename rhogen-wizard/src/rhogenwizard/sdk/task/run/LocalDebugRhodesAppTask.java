package rhogenwizard.sdk.task.run;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.ILaunch;
import rhogenwizard.PlatformType;
import rhogenwizard.RunType;
import rhogenwizard.StringUtils;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.IDebugTask;
import rhogenwizard.sdk.task.IRunTask;
import rhogenwizard.sdk.task.RubyDebugTask;
import rhogenwizard.sdk.task.RubyExecTask;
import rhogenwizard.sdk.task.SeqDebugTask;

public class LocalDebugRhodesAppTask extends SeqDebugTask
{
    private static interface IArgsBuilder
    {
        String[] getArgs(String stage);
    }

    public LocalDebugRhodesAppTask(ILaunch launch, RunType runType, String workDir,
        String appName, PlatformType platformType, boolean isReloadCode, boolean isTrace,
        String startPathOverride, String[] additionalRubyExtensions)
    {
        super(getArgs(launch, runType, workDir, appName, platformType, isReloadCode, isTrace,
            startPathOverride, additionalRubyExtensions));
    }

    private static SeqDebugTask.Args getArgs(ILaunch launch, final RunType runType, String workDir,
        String appName, final PlatformType platformType, final boolean reloadCode, final boolean trace,
        final String startPathOverride, final String[] additionalRubyExtensions)
    {
        IArgsBuilder ab = new IArgsBuilder()
        {
            @Override
            public String[] getArgs(String stage)
            {
                List<String> args = new ArrayList<String>();
                args.add("rake");

                if (runType == RunType.eRhoSimulator)
                {
                    args.add(StringUtils.join(":", "run", platformType.toString(), "rhosimulator", stage));
                }
                else if(runType == RunType.eSimulator)
                {
                    // for emulator
                    args.add(StringUtils.join(":", "run", platformType.toString(), stage));
                    args.add("rho_remote_debug=true");
                }
                else if(runType == RunType.eDevice)
                {
                    // for device
                    args.add(StringUtils.join(":", "run", platformType.toString(), "device", stage));
                    args.add("rho_remote_debug=true");
                }
                else
                {
                    return null;
                }

                if (trace)
                {
                    args.add("--trace");
                }

                args.add("rho_debug_port=9000");
                args.add("rho_reload_app_changes=" + (reloadCode ? "1" : "0"));

                if (startPathOverride != null)
                {
                    args.add("rho_override_start_path=\'" + startPathOverride + "\'");
                }

                if (additionalRubyExtensions != null && additionalRubyExtensions.length > 0)
                {
                    args.add("rho_extensions=" + StringUtils.join(",", additionalRubyExtensions));
                }

                return args.toArray(new String[0]);
            }
        };
        
        IRunTask buildTask = new RubyExecTask(workDir, SysCommandExecutor.RUBY_BAT,
            ab.getArgs("build"));
        IDebugTask debugTask = new RubyDebugTask(launch, appName, workDir,
            SysCommandExecutor.RUBY_BAT, ab.getArgs("debug"));

        return new SeqDebugTask.Args(new IRunTask[]{ buildTask }, debugTask);
    }
}
