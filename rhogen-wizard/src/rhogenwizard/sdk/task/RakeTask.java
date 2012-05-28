package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import rhogenwizard.OSHelper;
import rhogenwizard.OSValidator;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.helper.ConsoleBuildAdapter;

public abstract class RakeTask extends RunTask
{
    protected String              m_rakeExe    = "rake";
    protected SysCommandExecutor  m_executor   = new SysCommandExecutor();
    protected Map<String, ?>      m_taskParams = null;
    protected Map<String, Object> m_taskResult = new HashMap<String, Object>();

    public RakeTask()
    {
        m_executor.setOutputLogDevice(new ConsoleBuildAdapter());
        m_executor.setErrorLogDevice(new ConsoleBuildAdapter());

        if (OSValidator.OSType.WINDOWS == OSValidator.detect())
        {
            m_rakeExe = m_rakeExe + ".bat";
        }
    }

    @Override
    public Map<String, ?> getResult()
    {
        return m_taskResult;
    }

    @Override
    public void run(IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            throw new StoppedException();
        }

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                exec();
            }
        });
        thread.start();

        while (thread.isAlive())
        {
            try
            {
                thread.join(100);
            }
            catch (InterruptedException e)
            {
                throw new StoppedException(e);
            }

            if (monitor.isCanceled())
            {
                stop();
                throw new StoppedException();
            }
        }
    }

    protected abstract void exec();

    protected void stop()
    {
        try
        {
            OSHelper.killProcess("ruby");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
