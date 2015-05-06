package rhogenwizard.wizards.rhoconnect;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import rhogenwizard.BuildInfoHolder;
import rhogenwizard.DialogUtils;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.constants.MsgConstants;
import rhogenwizard.constants.UiConstants;
import rhogenwizard.project.IRhomobileProject;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhoconnectProject;
import rhogenwizard.sdk.task.RunTask;
import rhogenwizard.sdk.task.generate.GenerateRhoconnectAppTask;

public class AppWizard extends Wizard implements INewWizard
{
    private AppWizardPage m_pageApp = null;

    /**
     * Constructor for SampleNewWizard.
     */
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
        m_pageApp = new AppWizardPage();
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

        String pathToApp = null;
        
        if (infoHolder.isInDefaultWs)
        {
            pathToApp = ProjectFactory.getInstance().getWorkspaceDir().toOSString();
        }
        else
        {
            pathToApp = infoHolder.appDir;
        }
        
        RunTask task = new GenerateRhoconnectAppTask(pathToApp, infoHolder.appName);
        task.run(monitor);

        if (!task.isOk())
        {
            throw new IOException(MsgConstants.errInstallRhosync);
        }
    }

    /**
     * The worker method. It will find the container, create the file if missing
     * or just replace its contents, and open the editor on the newly created
     * file.
     * 
     * @throws ProjectNotFoundExtension
     */
    private void doFinish(BuildInfoHolder infoHolder, IProgressMonitor monitor)
    {
    	if (!infoHolder.isProjectPathValid())
    	{
    		DialogUtils.error("Error", "You can't create application on path with spaces. Change applicaiton name or path to workspace.");
    		monitor.done();
    		return;
    	}
    	
        try
        {
            monitor.beginTask("Creating " + infoHolder.appName, 2);
            monitor.worked(1);
            monitor.setTaskName("Create project...");

            if (!infoHolder.existCreate)
            {
                createProjectFiles(infoHolder, monitor);
            }
            
            IRhomobileProject newProject = ProjectFactory.getInstance().createProject(
                RhoconnectProject.class, infoHolder);

            newProject.refreshProject();

            ShowPerspectiveJob job = new ShowPerspectiveJob(
                "show rhodes perspective", UiConstants.rhodesPerspectiveId);
            job.schedule();

            monitor.worked(1);
        }
        catch (IOException e)
        {
            DialogUtils.error("Error", MsgConstants.errFindRhosync);
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

    /**
     * We will accept the selection in the workbench to see if we can initialize
     * from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
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
