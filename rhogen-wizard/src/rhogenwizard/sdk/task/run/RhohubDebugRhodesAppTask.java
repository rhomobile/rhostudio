package rhogenwizard.sdk.task.run;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.ILaunch;
import rhogenwizard.CloudUtils;
import rhogenwizard.PlatformType;
import rhogenwizard.RunType;
import rhogenwizard.StringUtils;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyDebugTask;
import rhogenwizard.sdk.task.RubyExecTask;
import rhogenwizard.sdk.task.SeqDebugTask;

public class RhohubDebugRhodesAppTask extends SeqDebugTask
{
    public RhohubDebugRhodesAppTask(ILaunch launch, RunType runType, String workDir,
        String appName, PlatformType platformType, boolean isTrace,
        String startPathOverride, String[] additionalRubyExtensions)
    {
        super(
            getBuildTask(workDir, CloudUtils.buildTask(platformType), isTrace, 
                startPathOverride, additionalRubyExtensions),
            getDebugTask(launch, runType, workDir, appName, platformType, isTrace,
                startPathOverride, additionalRubyExtensions)
        );
    }
    
    private static RubyExecTask getBuildTask(String workDir, String task, boolean isTrace,
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
            cmdLine.add("rho_extensions=" + StringUtils.join(",", additionalRubyExtensions));
        }

        return new RubyExecTask(
            workDir, SysCommandExecutor.RUBY_BAT, cmdLine.toArray(new String[0]));
    }

    private static RubyDebugTask getDebugTask(ILaunch launch, RunType runType, String workDir,
        String appName, PlatformType platformType, boolean isTrace, String startPathOverride,
        String[] additionalRubyExtensions)
    {
        List<String> args = new ArrayList<String>();
        args.add("rake");
        
        args.add(CloudUtils.runTask(runType));
        
        args.add("rho_remote_debug=true");

        if (isTrace)
        {
            args.add("--trace");
        }

        args.add("rho_debug_port=9000");
        
        if (startPathOverride != null)
        {
            args.add("rho_override_start_path=\'" + startPathOverride + "\'");
        }

        if (additionalRubyExtensions != null && additionalRubyExtensions.length > 0)
        {
            args.add("rho_extensions=" + StringUtils.join(",", additionalRubyExtensions));
        }
        
        return new RubyDebugTask(launch, appName, workDir, SysCommandExecutor.RUBY_BAT,
            args.toArray(new String[0]));
    }
}
