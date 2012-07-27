package rhogenwizard.sdk.task.rhohub;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.json.JSONException;

import rhogenwizard.DialogUtils;
import rhogenwizard.HttpDownload;
import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.rhohub.RemoteAppBuildDesc;
import rhogenwizard.rhohub.RemoteProjectDesc;
import rhogenwizard.rhohub.RemoteStatus;
import rhogenwizard.rhohub.RhoHub;
import rhogenwizard.rhohub.RhoHubBundleSetting;
import rhogenwizard.sdk.task.RunTask;

public class CheckBuildStatusTask extends RunTask
{
    private static int waitSleep = 500;
    
    private RemoteProjectDesc  m_project = null;
    private RemoteAppBuildDesc m_buildInfo = null;
    private AtomicBoolean      m_status = new AtomicBoolean(false);
    
    private final String m_dstDir;
    
    public CheckBuildStatusTask(RemoteProjectDesc project, RemoteAppBuildDesc buildInfo, final String dstDir)
    {
        m_project   = project;
        m_buildInfo = buildInfo;
        m_dstDir    = dstDir;
    }
    
    @Override
    public boolean isOk()
    {
        return m_status.get();
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
                
                while(m_buildInfo.getStatus() == RemoteStatus.eQueued || m_buildInfo.getStatus() == RemoteStatus.eStarted)
                {
                    m_status.getAndSet(RhoHub.getInstance(store).checkProjectBuildStatus(m_project, m_buildInfo));

                    if(monitor.isCanceled())
                        break;
                    
                    Thread.sleep(waitSleep);
                }
                monitor.worked(1);
                
                if (m_status.get())
                {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    HttpDownload hd = new HttpDownload(m_buildInfo.getBuildResultUrl(), os);
                    hd.join(0);
                    
                    File resultFile = new File(m_dstDir + File.separator + m_buildInfo.getBuildResultFileName());
                    FileOutputStream foStream = new FileOutputStream(resultFile);
                    os.writeTo(foStream);
                    
                    foStream.close();
                    os.close();
                    
                    monitor.worked(1);
                    
                    if (DialogUtils.confirm("Build Finished", m_project.getName() + " RhoHub build is complete. Open the build?"))
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
