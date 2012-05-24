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

public abstract class RakeTask implements IRunTask
{
    protected String m_rakeExe = "rake";
    protected SysCommandExecutor m_executor = new SysCommandExecutor();
    protected Map<String, ?> m_taskParams = null;
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

    public Map<String, ?> run(IProgressMonitor monitor) throws InterruptedException
    {
        Thread thread = new Thread(this);
        thread.start();
        while (thread.isAlive())
        {
            thread.join(100);
            if (monitor.isCanceled())
            {
                stop();
                throw new InterruptedException();
            }
        }
        return getResult();
    }

    public Job makeJob(String name)
    {
        return new Job(name)
        {
            @Override
            protected void canceling()
            {
                stop();
                super.canceling();
            }

            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
                try
                {
                    RakeTask.this.run(monitor);
                }
                catch (InterruptedException e)
                {
                    return Status.CANCEL_STATUS;
                }
                return Status.OK_STATUS;
            }
        };
    }
}
