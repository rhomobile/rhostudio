package rhogenwizard.sdk.task.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import rhogenwizard.PlatformType;
import rhogenwizard.RunType;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.IDebugTask;
import rhogenwizard.sdk.task.IRunTask;
import rhogenwizard.sdk.task.RubyDebugTask;
import rhogenwizard.sdk.task.RubyExecTask;
import rhogenwizard.sdk.task.SeqRunTask;

public class LocalDebugRhodesAppTask implements IDebugTask
{
    private final IDebugTask m_debugTask;
    private final SeqRunTask m_seqTask;

    public LocalDebugRhodesAppTask(ILaunch launch, RunType runType, String workDir,
        String appName, PlatformType platformType, boolean isReloadCode, boolean isTrace,
        String startPathOverride, String[] additionalRubyExtensions)
            throws IOException
    {
        File debugCommandFile = File.createTempFile("debug_command_", null);
        debugCommandFile.deleteOnExit();
        String dcf = debugCommandFile.getPath();

        IRunTask buildTask = new RubyExecTask(workDir, SysCommandExecutor.RUBY_BAT,
            getBuildArgs(platformType, runType, isTrace, isReloadCode, startPathOverride,
                additionalRubyExtensions, dcf));
        m_debugTask = getDebugTask(launch, workDir, appName, dcf);

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

    private static String[] getBuildArgs(PlatformType platformType, RunType runType,
        boolean isReloadCode, boolean isTrace, String startPathOverride,
        String[] additionalRubyExtensions, String debugCommandFile)
    {
        List<String> args = new ArrayList<String>();
        args.add("rake");
        
        if (runType == RunType.eRhoSimulator)
        {
            args.add("run:" + platformType + ":rhosimulator_debug[" + debugCommandFile + "]");
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

    private static IDebugTask getDebugTask(final ILaunch launch, final String workDir,
        final String appName, final String debugCommandFile)
    {
        return new IDebugTask()
        {
            private IDebugTask m_task = null;

            @Override
            public void run()
            {
                init().run();
            }

            @Override
            public void run(IProgressMonitor monitor)
            {
                init().run(monitor);
            }

            @Override
            public boolean isOk()
            {
                return get().isOk();
            }

            @Override
            public IProcess getDebugProcess()
            {
                return get().getDebugProcess();
            }

            private IDebugTask init()
            {
                String[] args;
                try
                {
                    args = readLinesFromFile(debugCommandFile);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                m_task = new RubyDebugTask(launch, appName, workDir, SysCommandExecutor.CRT, args);
                return m_task;
            }

            private IDebugTask get()
            {
                if (m_task == null)
                {
                    throw new IllegalStateException("The task is not finished yet.");
                }
                return m_task;
            }
        };
    }

    private static String[] readLinesFromFile(String path) throws IOException
    {
        List<String> lines = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(path)))
        {
            while (true)
            {
                String line = br.readLine();
                if (line == null)
                {
                    break;
                }
                lines.add(line);
            }
        }
        return lines.toArray(new String[0]);
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
