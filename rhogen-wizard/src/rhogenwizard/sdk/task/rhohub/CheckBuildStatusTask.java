package rhogenwizard.sdk.task.rhohub;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.json.JSONException;

import rhogenwizard.Activator;
import rhogenwizard.rhohub.IRemoteProjectDesc;
import rhogenwizard.rhohub.RemoteStatus;
import rhogenwizard.rhohub.RhoHub;
import rhogenwizard.sdk.task.RunTask;

public class CheckBuildStatusTask extends RunTask
{
    IRemoteProjectDesc m_project = null;
    AtomicBoolean      m_status = new AtomicBoolean(false);
    
    public CheckBuildStatusTask(IRemoteProjectDesc project)
    {
        m_project = project;
    }
    
    @Override
    public boolean isOk()
    {
        return m_status.get();
    }

    @Override
    public void run(IProgressMonitor monitor)
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        if (store != null)
        {
            try
            {
                while(m_project.getBuildStatus() == RemoteStatus.eQueued || m_project.getBuildStatus() == RemoteStatus.eStarted)
                {
                    m_status.getAndSet(RhoHub.getInstance(store).checkProjectBuildStatus(m_project));

                    if(monitor.isCanceled())
                        break;
                }
            }
            catch (JSONException e)
            {
                m_status.getAndSet(false);
                
                e.printStackTrace();
            }
        }        
    }
}
