package rhogenwizard.sdk.job;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import rhogenwizard.Activator;
import rhogenwizard.sdk.helper.TaskResultConverter;
import rhogenwizard.sdk.task.GenerateRhodesAppTask;

public class GenerateRhodesAppJob extends Job
{
    private final String m_appName;
    private final String m_projectLocation;

    public GenerateRhodesAppJob(String appName, String projectLocation)
    {
        super("GenerateRhodesApp");
        m_appName = appName;
        m_projectLocation = projectLocation;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put(GenerateRhodesAppTask.appName, m_appName);
        params.put(GenerateRhodesAppTask.workDir, m_projectLocation);

        GenerateRhodesAppTask task = new GenerateRhodesAppTask();
        task.setData(params);

        int severity = IStatus.ERROR;
        try
        {
            Map<String, ?> results = task.start().waitFor();
            severity =
                    (TaskResultConverter.getResultIntCode(results) == 0) ? IStatus.OK
                            : IStatus.ERROR;
        }
        catch (IOException e)
        {
        }
        catch (InterruptedException e)
        {
        }

        return new Status(severity, Activator.PLUGIN_ID, "");
    }
}
