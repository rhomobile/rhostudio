package rhogenwizard.sdk.task.run;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import rhogenwizard.PlatformType;
import rhogenwizard.RunType;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.IDebugTask;
import rhogenwizard.sdk.task.RubyDebugTask;

public class LocalDebugRhodesAppTask implements IDebugTask
{
    private final RubyDebugTask m_debugTask;

    public LocalDebugRhodesAppTask(ILaunch launch, RunType runType, String workDir,
        String appName, PlatformType platformType, boolean isReloadCode, boolean isTrace,
        String startPathOverride, String[] additionalRubyExtensions)
    {
        m_debugTask = new RubyDebugTask(launch, appName, workDir,
            SysCommandExecutor.RUBY_BAT, getArgs(platformType,
            runType, isTrace, isReloadCode, startPathOverride, additionalRubyExtensions));
    }

    public LocalDebugRhodesAppTask sync()
    {
        m_debugTask.sync();
        return this;
    }

    @Override
    public boolean isOk()
    {
        return m_debugTask.isOk();
    }

    @Override
    public void run(IProgressMonitor monitor)
    {
        m_debugTask.run(monitor);
    }

    @Override
    public void run()
    {
        m_debugTask.run();
    }

    @Override
    public IProcess getDebugProcess()
    {
        return m_debugTask.getDebugProcess();
    }

    private static String[] getArgs(PlatformType platformType, RunType runType,
        boolean isReloadCode, boolean isTrace, String startPathOverride,
        String[] additionalRubyExtensions)
    {
        List<String> args = new ArrayList<String>();
        args.add("rake");
        
        if (runType == RunType.eRhoSimulator)
        {
        	args.add("run:" + platformType + ":rhosimulator_debug");
        }
        else if(runType == RunType.eSimulator)
        {
        	// for emulator
        	args.add("run:" + platformType);
            args.add("rho_remote_debug=true");
        }
        else if(runType == RunType.eDevice)
        {
        	// for device
        	args.add("run:" + platformType + ":device");
            args.add("rho_remote_debug=true");
        }
        else
        {
        	return null;
        }

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
