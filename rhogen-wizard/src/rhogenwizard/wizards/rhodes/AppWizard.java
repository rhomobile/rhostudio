package rhogenwizard.wizards.rhodes;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import rhogenwizard.BuildInfoHolder;
import rhogenwizard.DialogUtils;
import rhogenwizard.RunExeHelper;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.constants.CommonConstants;
import rhogenwizard.constants.MsgConstants;
import rhogenwizard.constants.UiConstants;
import rhogenwizard.project.IRhomobileProject;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.extension.AlredyCreatedException;
import rhogenwizard.sdk.task.RunTask;
import rhogenwizard.sdk.task.generate.GenerateRhodesAppTask;
import rhogenwizard.sdk.task.generate.GenerateRhoelementsAppTask;
import rhogenwizard.wizards.BaseAppWizard;

public class AppWizard extends BaseAppWizard
{
    private AppWizardPage m_pageApp = null;

    public AppWizard()
    {
        super();
        setNeedsProgressMonitor(true);
    }

    /**
     * Adding the page to the wizard.
     */
    public void addPages()
    {
        m_pageApp = new AppWizardPage(this);
        addPage(m_pageApp);
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We
     * will create an operation and run it using wizard as execution context.
     */
    public boolean performFinish()
    {
        final BuildInfoHolder holder = m_pageApp.getBuildInformation();

        IRunnableWithProgress op = new IRunnableWithProgress()
        {
            public void run(IProgressMonitor monitor) throws InvocationTargetException
            {
                try
                {
                    doFinish(holder, monitor);
                }
                finally
                {
                    monitor.done();
                }
            }
        };

        try
        {
            getContainer().run(true, true, op);
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

    private void createProjectFiles(BuildInfoHolder infoHolder, IProgressMonitor monitor) throws IOException
    {
        monitor.setTaskName("Generate application...");

        RunTask task = null;
        String  pathToApp = null;
        
        if (infoHolder.isInDefaultWs)
        {
            pathToApp = ProjectFactory.getInstance().getWorkspaceDir().toOSString();
        }
        else
        {
            pathToApp = infoHolder.appDir;
        }
        
        if (infoHolder.isRhoelementsApp)
        {
            task = new GenerateRhoelementsAppTask(pathToApp, infoHolder.appName);
        }
        else
        {
            task = new GenerateRhodesAppTask(pathToApp, infoHolder.appName);
        }

        task.run(monitor);

        if (!task.isOk())
        {
            throw new IOException(MsgConstants.errInstallRhodes);
        }
    }

    /**
     * @throws ProjectNotFoundExtension
     *             The worker method. It will find the container, create the
     *             file if missing or just replace its contents, and open the
     *             editor on the newly created file.
     * @throws
     */
    private void doFinish(BuildInfoHolder infoHolder, IProgressMonitor monitor)
    {
    	// comment for SPR 23611
//    	if (!infoHolder.isProjectPathValid())
//    	{
//    		DialogUtils.error("Error", "You can't create application on path with spaces. Change applicaiton name or path to workspace.");
//    		monitor.done();
//    		return;
//    	}
    	
        try
        {
            monitor.beginTask("Creating " + infoHolder.appName, 2);
            monitor.worked(1);

            if (CommonConstants.checkRhodesVersion)
            {
                monitor.setTaskName("Check Rhodes version...");

                try
                {
                    if (!RunExeHelper.checkRhodesVersion(CommonConstants.rhodesVersion))
                    {
                        throw new IOException();
                    }
                }
                catch (IOException e)
                {
                    String msg = "Installed Rhodes have old version, need rhodes version equal or greater " 
                        + CommonConstants.rhodesVersion + " Please reinstall it (See 'http://docs.rhomobile.com/rhodes/install' for more information)";
                    DialogUtils.error("Error", msg);
                    return;
                }
            }

            if (!infoHolder.existCreate)
            {
                createProjectFiles(infoHolder, monitor);
            }
            
            monitor.setTaskName("Create project...");
            IRhomobileProject newProject = ProjectFactory.getInstance().createProject(RhodesProject.class, infoHolder);
            newProject.refreshProject();
            
            ShowPerspectiveJob job = new ShowPerspectiveJob("show rhodes perspective",
                UiConstants.rhodesPerspectiveId);
            job.schedule();

            monitor.worked(1);
        }
        catch (IOException e)
        {
        	String msg = null;
        	
        	if (infoHolder.isRhoelementsApp)
        	{
                msg = "Cannot find Rhoelements, install the gem before generate Rhoelements applications."; 
        	}
        	else
        	{
                msg = "Cannot find Rhodes, need version equal or greater " 
                        + CommonConstants.rhodesVersion + " (See 'http://docs.rhomobile.com/rhodes/install' for more information)";
        	}

        	DialogUtils.error("Error", msg);
        }
        catch (AlredyCreatedException e)
        {
            DialogUtils.warning("Warning", e.toString());
        }
        catch (CoreException e)
        {
            if (!showError(e.getStatus()))
            {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static boolean showError(IStatus status)
    {
        if (status.getSeverity() == IStatus.ERROR)
        {
            if (status.isMultiStatus())
            {
                for (IStatus child : status.getChildren())
                {
                    if (showError(child))
                    {
                        return true;
                    }
                }
            }
            DialogUtils.error("Error", status.getMessage());
            return true;
        }
        return false;
    }
}
