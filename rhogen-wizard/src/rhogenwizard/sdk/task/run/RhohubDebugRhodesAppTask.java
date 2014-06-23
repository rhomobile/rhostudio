package rhogenwizard.sdk.task.run;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import rhogenwizard.CloudUtils;
import rhogenwizard.PlatformType;
import rhogenwizard.RunType;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.IDebugTask;
import rhogenwizard.sdk.task.RubyDebugTask;
import rhogenwizard.sdk.task.RubyExecTask;
import rhogenwizard.sdk.task.SeqRunTask;

public class RhohubDebugRhodesAppTask implements IDebugTask
{
    private final IDebugTask m_lastTask;
    private final SeqRunTask m_seqTask;
    
    public RhohubDebugRhodesAppTask(ILaunch launch, RunType runType, String workDir,
        String appName, PlatformType platformType, boolean isTrace,
        String startPathOverride, String[] additionalRubyExtensions)
    {
        m_lastTask = getDebugTask(launch, runType, workDir, appName, platformType, isTrace,
            startPathOverride, additionalRubyExtensions);
        m_seqTask = new SeqRunTask(
            getBuildTask(workDir, CloudUtils.buildTask(platformType), isTrace,
                startPathOverride, additionalRubyExtensions),
            m_lastTask
        );
    }

    @Override
    public boolean isOk()
    {
        return m_seqTask.isOk();
    }

    @Override
    public void run()
    {
        m_seqTask.run();
    }

    @Override
    public void run(IProgressMonitor monitor)
    {
        m_seqTask.run(monitor);
    }
    
    @Override
    public IProcess getDebugProcess()
    {
        return m_lastTask.getDebugProcess();
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
            cmdLine.add("rho_extensions=" + join(",", additionalRubyExtensions));
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
            args.add("rho_extensions=" + join(",", additionalRubyExtensions));
        }
        
        return new RubyDebugTask(launch, appName, workDir, SysCommandExecutor.RUBY_BAT,
            args.toArray(new String[0]));
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
