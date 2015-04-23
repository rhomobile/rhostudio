package rhogenwizard.sdk.task;

import java.io.IOException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;

import rhogenwizard.Activator;
import rhogenwizard.ConsoleHelper;
import rhogenwizard.ILogDevice;
import rhogenwizard.SysCommandExecutor;

public class RubyDebugTask extends RubyTask implements IDebugTask
{
    private final ILaunch               m_launch;
    private final String                m_appName;
    private final ConsoleHelper.Console m_console;
    private boolean                     m_sync;
    private IProcess                    m_debugProcess;
    private Integer                     m_exitValue;

    public RubyDebugTask(ILaunch launch, String appName, String workDir,
        SysCommandExecutor.Decorator decorator, String... args)
    {
        super(workDir, decorator, args);

        m_launch  = launch;
        m_appName = appName;
        m_console = ConsoleHelper.getBuildConsole();

        m_sync         = false;
        m_debugProcess = null;
        m_exitValue    = null;
    }

    @Override
    public boolean isOk()
    {
        return (m_sync) ? m_exitValue != null : m_debugProcess != null;
    }

    @Override
    public IProcess getDebugProcess()
    {
        return m_debugProcess;
    }

    @Override
    public void exec()
    {
        m_console.show();
        m_console.getStream().print(showCommand());
    	
        SysCommandExecutor executor = new SysCommandExecutor();
        executor.setOutputLogDevice(getLogDevice(m_console.getOutputStream()));
        executor.setErrorLogDevice(getLogDevice(m_console.getErrorStream()));

        executor.setWorkingDirectory(m_workDir);

        Process process;
        try
        {
            process = executor.startCommand(m_decorator, m_cmdLine, null);
        }
        catch (IOException e)
        {
            Activator.logError(e);
            return;
        }

        m_debugProcess = DebugPlugin.newProcess(m_launch, process, m_appName);
                
        if (m_debugProcess != null)
        {
            attachConsole(m_debugProcess, m_console);
        }

        new Job("Bring console back.")
        {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
                m_console.show();
                return Status.OK_STATUS;
            }
        }.schedule(1000);
        
        if (m_sync)
        {
            try
            {
                m_exitValue = process.waitFor();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
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

    private static ILogDevice getLogDevice(final ConsoleHelper.Stream stream)
    {
        return new ILogDevice()
        {
            @Override
            public void log(String str)
            {
                stream.println(str.replaceAll("\\p{Cntrl}", " "));
            }
        };
    }
}
