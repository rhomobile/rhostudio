package rhogenwizard.sdk.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.PlatformType;

public class RunDebugRhodesAppTask extends RubyTask
{
    private final String       m_workDir;
    private final String       m_appName;
    private final PlatformType m_platformType;
    private final boolean      m_isReloadCode;
    private final ILaunch      m_launch;
    private final boolean      m_isTrace;
    private IProcess           m_debugProcess;

    public RunDebugRhodesAppTask(String workDir, String appName, PlatformType platformType,
        boolean isReloadCode, ILaunch launch, boolean isTrace)
    {
        m_workDir = workDir;
        m_appName = appName;
        m_platformType = platformType;
        m_isReloadCode = isReloadCode;
        m_launch = launch;
        m_isTrace = isTrace;
        m_debugProcess = null;
    }

    @Override
    public boolean isOk()
    {
        return m_debugProcess != null;
    }

    public IProcess getDebugProcess()
    {
        return m_debugProcess;
    }

    @Override
    protected void exec()
    {
        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add(getCommand("rake"));
        cmdLine.add("run:" + m_platformType + ":rhosimulator_debug");

        if (m_isTrace)
        {
            cmdLine.add("--trace");
        }

        cmdLine.add("rho_debug_port=9000");
        cmdLine.add("rho_reload_app_changes=" + (m_isReloadCode ? "1" : "0"));

        String[] commandLine = cmdLine.toArray(new String[0]);

        Process process;
        try
        {
            process = DebugPlugin.exec(commandLine, new File(m_workDir));
        }
        catch (CoreException e)
        {
            return;
        }

        m_debugProcess = DebugPlugin.newProcess(m_launch, process, m_appName);

        if (m_debugProcess != null)
        {
            attachConsole(m_debugProcess, ConsoleHelper.getBuildConsole());
        }
    }

    public static void attachConsole(IProcess process, ConsoleHelper.Console console)
    {
        process.getStreamsProxy().getErrorStreamMonitor()
            .addListener(getStreamListener(console.getErrorStream()));
        process.getStreamsProxy().getOutputStreamMonitor()
            .addListener(getStreamListener(console.getOutputStream()));
    }

    private static IStreamListener getStreamListener(final ConsoleHelper.Stream stream)
    {
        return new IStreamListener()
        {
            @Override
            public void streamAppended(String text, IStreamMonitor monitor)
            {
                stream.println(text);
            }
        };
    }
}
