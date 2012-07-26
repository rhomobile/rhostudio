package rhogenwizard.wizards.rhohub;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import rhogenwizard.DialogUtils;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.constants.CommonConstants;
import rhogenwizard.constants.UiConstants;
import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.rhohub.RemoteAppBuildDesc;
import rhogenwizard.rhohub.RemoteProjectDesc;
import rhogenwizard.rhohub.RhoHub;
import rhogenwizard.rhohub.RhoHubBundleSetting;
import rhogenwizard.sdk.task.rhohub.CheckBuildStatusTask;
import rhogenwizard.wizards.BaseAppWizard;

public class BuildWizard extends BaseAppWizard
{
    private BuildSettingPage    m_pageSetting = null;
    private IProject            m_selectedProject = null;
    
    public BuildWizard(IProject project)
    {
        super();
        setNeedsProgressMonitor(true);
        
        m_selectedProject = project;
    }

    /**
     * Adding the page to the wizard.
     */
    public void addPages()
    {       
        m_pageSetting = new BuildSettingPage(m_selectedProject);
        
        addPage(m_pageSetting);
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We
     * will create an operation and run it using wizard as execution context.
     */
    public boolean performFinish()
    {
        final String dstDir = getSelectedDirectory();
        
        IRunnableWithProgress op = new IRunnableWithProgress()
        {
            public void run(IProgressMonitor monitor) throws InvocationTargetException
            {
                try
                {
                    doFinish(dstDir, monitor);
                }
                finally
                {
                    monitor.done();
                }
            }
        };

        try
        {
            getContainer().run(true, false, op);
        }
        catch (InterruptedException e)
        {
            return false;
        }
        catch (InvocationTargetException e)
        {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }

        return true;
    }

    private String getSelectedDirectory()
    {
        DirectoryDialog dlg = new DirectoryDialog(Display.getCurrent().getActiveShell());

        dlg.setFilterPath("C:");
        dlg.setText("Select destination directory");
        dlg.setMessage("Select a directory");

        return dlg.open();
    }
    
    /**
     * @throws ProjectNotFoundExtension
     *             The worker method. It will find the container, create the
     *             file if missing or just replace its contents, and open the
     *             editor on the newly created file.
     */
    private void doFinish(final String dstDir, IProgressMonitor monitor)
    {
        try
        {
            monitor.beginTask("Start building on rhohub server", 1);
            
            if (CommonConstants.checkRhohubVersion)
            {
//                monitor.setTaskName("Check Rhodes version...");
//
//                try
//                {
//                    if (!RunExeHelper.checkRhodesVersion(CommonConstants.rhodesVersion))
//                    {
//                        throw new IOException();
//                    }
//                }
//                catch (IOException e)
//                {
//                    String msg = "Installed Rhohub have old version, need rhodes version equal or greater " 
//                        + CommonConstants.rhodesVersion + " Please reinstall it (See 'http://docs.rhomobile.com/rhodes/install' for more information)";
//                    DialogUtils.error("Error", msg);
//                    return;
//                }
            }            
            monitor.worked(1);
            
            IRhoHubSetting store = RhoHubBundleSetting.createGetter(m_selectedProject);

            if (store != null)
            {
                RemoteProjectDesc  projectDesc = RhoHub.getInstance(store).findRemoteApp(m_selectedProject);
                RemoteAppBuildDesc buildInfo   = null;
                
                if (projectDesc != null)
                {
                    buildInfo = RhoHub.getInstance(store).buildRemoteApp(projectDesc);
                    
                    if (buildInfo == null)
                    {
                        DialogUtils.error("Error", "Build is failed.");
                        monitor.done();
                        return;
                    }
                }                       
                monitor.worked(1);
                
                monitor.beginTask("Start checking build on rhohub server", 1);

                CheckBuildStatusTask checkTask = new CheckBuildStatusTask(projectDesc, buildInfo, dstDir);
                Job checkJob = checkTask.makeJob("Checking remote build status");
                checkJob.schedule();
            }
            
            ShowPerspectiveJob job = new ShowPerspectiveJob("show rhodes perspective",
                UiConstants.rhodesPerspectiveId);
            job.schedule();

            monitor.done();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}