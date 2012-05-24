package rhogenwizard.wizards.rhoconnect;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.runtime.CoreException;
import java.io.*;

import org.eclipse.ui.*;
import rhogenwizard.BuildInfoHolder;
import rhogenwizard.DialogUtils;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.constants.MsgConstants;
import rhogenwizard.constants.UiConstants;
import rhogenwizard.project.IRhomobileProject;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhoconnectProject;
import rhogenwizard.project.extension.AlredyCreatedException;
import rhogenwizard.project.extension.CheckProjectException;
import rhogenwizard.project.extension.ProjectNotFoundException;
import rhogenwizard.sdk.facade.RhoTaskHolder;
import rhogenwizard.sdk.helper.TaskResultConverter;
import rhogenwizard.sdk.task.GenerateRhoconnectAppTask;

public class AppWizard extends Wizard implements INewWizard
{
    private static final String okRhodesVersionFlag = "1";

    private AppWizardPage m_pageApp = null;
    private ISelection selection = null;

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
        m_pageApp = new AppWizardPage(selection);
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

    private void createProjectFiles(BuildInfoHolder infoHolder, IProgressMonitor monitor)
            throws IOException, Exception
    {
        monitor.setTaskName("Generate application...");

        Map<String, Object> params = new HashMap<String, Object>();

        params.put(GenerateRhoconnectAppTask.appName, infoHolder.appName);
        params.put(GenerateRhoconnectAppTask.workDir, infoHolder.getProjectLocationPath()
                .toOSString());

        Map results =
                RhoTaskHolder.getInstance().runTask(GenerateRhoconnectAppTask.class, params);

        if (TaskResultConverter.getResultIntCode(results) != 0)
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

            newProject =
                    ProjectFactory.getInstance().createProject(RhoconnectProject.class,
                            infoHolder);

            if (!infoHolder.existCreate)
            {
                createProjectFiles(infoHolder, monitor);
            }

            newProject.refreshProject();

            ShowPerspectiveJob job =
                    new ShowPerspectiveJob("show rhodes perspective",
                            UiConstants.rhodesPerspectiveId);
            job.schedule();

            monitor.worked(1);
        }
        catch (IOException e)
        {
            newProject.deleteProjectFiles();
            DialogUtils.error("Error", MsgConstants.errFindRhosync);
        }
        catch (CheckProjectException e)
        {
            DialogUtils.error("Error", e.getMessage());
        }
        catch (AlredyCreatedException e)
        {
            DialogUtils.warn("Warning", e.toString());
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
        this.selection = selection;
    }
}
