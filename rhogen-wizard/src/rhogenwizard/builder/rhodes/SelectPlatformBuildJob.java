package rhogenwizard.builder.rhodes;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import rhogenwizard.PlatformType;
import rhogenwizard.sdk.facade.RhoTaskHolder;
import rhogenwizard.sdk.task.BuildPlatformTask;

public class SelectPlatformBuildJob extends Job
{
    private PlatformType m_selectPlatform = PlatformType.eUnknown;
    private String m_workDir = null;

    public SelectPlatformBuildJob(String name, String workDir, PlatformType plType)
    {
        super(name);

        m_workDir = workDir;
        m_selectPlatform = plType;
    }

    @Override
    protected void canceling()
    {
        RhoTaskHolder.getInstance().stopTask(BuildPlatformTask.class);
        super.canceling();
    }

    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(BuildPlatformTask.workDir, m_workDir);
        params.put(BuildPlatformTask.platformType, m_selectPlatform);

        RhoTaskHolder.getInstance().runTask(BuildPlatformTask.class, params);

        return Status.OK_STATUS;
    }
}
