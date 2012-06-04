package rhogenwizard.wizards.rhoconnect;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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
import rhogenwizard.project.extension.AlredyCreatedException;
import rhogenwizard.project.extension.ProjectNotFoundException;
import rhogenwizard.sdk.task.GenerateRhoconnectAppTask;
import rhogenwizard.sdk.task.RunTask;

public class AppWizard extends Wizard implements INewWizard
{
    private static final String okRhodesVersionFlag = "1";

    private AppWizardPage        m_pageApp = null;
    private IStructuredSelection m_selection = null;

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
        m_pageApp = new AppWizardPage(m_selection);
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
                catch (CoreException e)
                {
                    throw new InvocationTargetException(e);
                }
                catch (ProjectNotFoundException e)
                {
                    e.printStackTrace();
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
            throws CoreException, ProjectNotFoundException
    {
        IRhomobileProject newProject = null;

        try
        {
            monitor.beginTask("Creating " + infoHolder.appName, 2);
            monitor.worked(1);
            monitor.setTaskName("Create project...");

            newProject = ProjectFactory.getInstance().createProject(RhoconnectProject.class, infoHolder);

            if (!infoHolder.existCreate)
            {
                createProjectFiles(infoHolder, monitor);
            }

            newProject.refreshProject();

            ShowPerspectiveJob job = new ShowPerspectiveJob(
                "show rhodes perspective", UiConstants.rhodesPerspectiveId);
            job.schedule();

            monitor.worked(1);
        }
        catch (IOException e)
        {
            newProject.deleteProjectFiles();
            DialogUtils.error("Error", MsgConstants.errFindRhosync);
        }
        catch (AlredyCreatedException e)
        {
            DialogUtils.warning("Warning", e.toString());
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
        this.m_selection = selection;
    }
}
