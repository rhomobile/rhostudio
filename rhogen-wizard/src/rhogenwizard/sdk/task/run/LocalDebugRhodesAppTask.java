package rhogenwizard.sdk.task.run;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import rhogenwizard.PlatformType;
import rhogenwizard.RunType;
import rhogenwizard.StringUtils;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.IDebugTask;
import rhogenwizard.sdk.task.IRunTask;
import rhogenwizard.sdk.task.RubyDebugTask;
import rhogenwizard.sdk.task.RubyExecTask;
import rhogenwizard.sdk.task.SeqRunTask;

public class LocalDebugRhodesAppTask implements IDebugTask
{
    private static interface IArgsBuilder
    {
        String[] getArgs(String stage);
    }

    private static IArgsBuilder makeArgsBuilder(final PlatformType platformType,
            final RunType runType, final boolean reloadCode, final boolean trace,
            final String startPathOverride, final String[] additionalRubyExtensions)
    {
        return new IArgsBuilder()
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
    }

    private final IDebugTask m_debugTask;
    private final SeqRunTask m_seqTask;

    public LocalDebugRhodesAppTask(ILaunch launch, RunType runType, String workDir,
        String appName, PlatformType platformType, boolean isReloadCode, boolean isTrace,
        String startPathOverride, String[] additionalRubyExtensions)
    {
        IArgsBuilder ab = makeArgsBuilder(platformType, runType, isReloadCode, isTrace,
                startPathOverride, additionalRubyExtensions);
        IRunTask buildTask = new RubyExecTask(workDir, SysCommandExecutor.RUBY_BAT,
            ab.getArgs("build"));
        m_debugTask = new RubyDebugTask(launch, appName, workDir, SysCommandExecutor.RUBY_BAT,
            ab.getArgs("debug"));

        m_seqTask = new SeqRunTask(buildTask, m_debugTask);
    }

    @Override
    public boolean isOk()
    {
        return m_seqTask.isOk();
    }

    @Override
    public void run(IProgressMonitor monitor)
    {
        m_seqTask.run(monitor);
    }

    @Override
    public void run()
    {
        m_seqTask.run();
    }

    @Override
    public IProcess getDebugProcess()
    {
        return m_debugTask.getDebugProcess();
    }
}
