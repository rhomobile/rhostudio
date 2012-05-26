package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import rhogenwizard.OSHelper;
import rhogenwizard.OSValidator;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.helper.ConsoleBuildAdapter;

public abstract class RakeTask extends RunTask
{
    public class StoppedException extends RuntimeException
    {
        private static final long serialVersionUID = -3189956590994196563L;

        public StoppedException()
        {
        }

        public StoppedException(Throwable e)
        {
            super(e);
        }
    }

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
    public void stop()
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

    @Override
    public void setData(Map<String, ?> data)
    {
        m_taskParams = data;
    }

    @Override
    public Map<String, ?> getResult()
    {
        return m_taskResult;
    }

    public String runRakeTask(String workDir, String taskName) throws Exception
    {
        m_executor.setWorkingDirectory(workDir);

        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add(m_rakeExe);
        cmdLine.add(taskName);

        m_executor.runCommand(cmdLine);

        return m_executor.getCommandOutput();
    }

    public void run(IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            throw new StoppedException();
        }

        Thread thread = new Thread(this);
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

    public Job makeJob(String name)
    {
        return new Job(name)
        {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
                try
                {
                    RakeTask.this.run(monitor);
                }
                catch (StoppedException e)
                {
                    return Status.CANCEL_STATUS;
                }
                return Status.OK_STATUS;
            }
        };
    }
}
