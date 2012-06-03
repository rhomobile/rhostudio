package rhogenwizard.sdk.task;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;

import rhogenwizard.ConsoleHelper;

public class RubyDebugTask extends RubyTask
{
    private final ILaunch               m_launch;
    private final String                m_appName;
    private final ConsoleHelper.Console m_console;
    private IProcess                    m_debugProcess;

    public RubyDebugTask(ILaunch launch, String appName, String workDir, String commandName, String... args)
    {
        super(workDir, commandName, args);

        m_launch = launch;
        m_appName = appName;
        m_console = ConsoleHelper.getAppConsole();

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
        m_console.show();
        m_console.getStream().print(showCommand());

        String[] commandLine = m_cmdLine.toArray(new String[0]);

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
            attachConsole(m_debugProcess, m_console);
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
