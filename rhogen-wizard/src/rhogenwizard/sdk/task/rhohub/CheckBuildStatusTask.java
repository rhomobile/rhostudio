package rhogenwizard.sdk.task.rhohub;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.json.JSONException;

import rhogenwizard.DialogUtils;
import rhogenwizard.HttpDownload;
import rhogenwizard.rhohub.IRemoteProjectDesc;
import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.rhohub.RemoteStatus;
import rhogenwizard.rhohub.RhoHub;
import rhogenwizard.rhohub.RhoHubBundleSetting;
import rhogenwizard.sdk.task.RunTask;

public class CheckBuildStatusTask extends RunTask
{
    private static int waitSleep = 100;
    
    private IRemoteProjectDesc m_project = null;
    private AtomicBoolean      m_status = new AtomicBoolean(false);
    
    public CheckBuildStatusTask(IRemoteProjectDesc project)
    {
        m_project = project;
    }
    
    @Override
    public boolean isOk()
    {
        return m_status.get();
    }

    private String getSelectedDirectory()
    {
        DirectoryDialog dlg = new DirectoryDialog(Display.getCurrent().getActiveShell());

        dlg.setFilterPath("C:");
        dlg.setText("Select destination directory");
        dlg.setMessage("Select a directory");

        return dlg.open();
    }
    
    @Override
    public void run(IProgressMonitor monitor)
    {
        IRhoHubSetting store = RhoHubBundleSetting.createGetter(m_project.getProject());

        if (store != null)
        {
            try
            {
                monitor.beginTask("Build status", 3);
                
                while(m_project.getBuildStatus() == RemoteStatus.eQueued || m_project.getBuildStatus() == RemoteStatus.eStarted)
                {
                    m_status.getAndSet(RhoHub.getInstance(store).checkProjectBuildStatus(m_project));

                    if(monitor.isCanceled())
                        break;
                    
                    Thread.sleep(waitSleep);
                }
                monitor.worked(1);
                
                if (m_status.get())
                {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    HttpDownload hd = new HttpDownload(m_project.getBuildResultUrl(), os);
                    hd.join(0);
                    
                    File resultFile = new File(getSelectedDirectory() + File.separator + m_project.getBuildResultFileName());
                    FileOutputStream foStream = new FileOutputStream(resultFile);
                    os.writeTo(foStream);
                    
                    foStream.close();
                    os.close();
                    
                    monitor.worked(1);
                    
                    if (DialogUtils.confirm("Build result", "File with application build is download to your computer, open file?"))
                    {
                        Desktop.getDesktop().open(resultFile);
                    }                    
                    monitor.worked(1);
                }
                
                monitor.done();
            }
            catch (IOException e)
            {
                m_status.getAndSet(false);
                e.printStackTrace();
            }   
            catch (JSONException e)
            {
                m_status.getAndSet(false);
                e.printStackTrace();
            }
            catch (InterruptedException e)
            {
                m_status.getAndSet(false);
                e.printStackTrace();
            }
        }        
    }
}
