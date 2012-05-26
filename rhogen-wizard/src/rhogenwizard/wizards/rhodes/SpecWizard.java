package rhogenwizard.wizards.rhodes;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import rhogenwizard.DialogUtils;
import rhogenwizard.RunExeHelper;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.constants.CommonConstants;
import rhogenwizard.constants.UiConstants;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;
import rhogenwizard.sdk.helper.TaskResultConverter;
import rhogenwizard.sdk.task.GenerateRhodesSpecTask;
import rhogenwizard.sdk.task.RakeTask;
import rhogenwizard.wizards.ZeroPage;

public class SpecWizard extends Wizard implements INewWizard
{
    private IProject m_currentProject = null;

    public SpecWizard()
    {
        super();
        setNeedsProgressMonitor(true);

        m_currentProject = ProjectFactory.getInstance().getSelectedProject();

        if (m_currentProject != null)
        {
            m_currentProject.getLocation().toOSString();
        }
    }

    /**
     * Constructor for SampleNewWizard.
     */
    public SpecWizard(IProject currentProject)
    {
        super();
        setNeedsProgressMonitor(true);

        m_currentProject = currentProject;
        m_currentProject.getLocation().toOSString();
    }

    /**
     * Adding the page to the wizard.
     */
    public void addPages()
    {
        if (m_currentProject != null)
        {
            if (!RhodesProject.checkNature(m_currentProject) && !RhoelementsProject.checkNature(m_currentProject))
            {
                ZeroPage zeroPage = new ZeroPage(
                	"Project " + m_currentProject.getName() + " is not RhoMobile application");
                addPage(zeroPage);
            }
            else
            {
                ZeroPage zeroPage = new ZeroPage(
                	"Press Finish button for finished generate spec framework files");
                addPage(zeroPage);
            }
        }
        else
        {
            ZeroPage zeroPage = new ZeroPage("Select RhoMobile project for create spec");
            addPage(zeroPage);
        }
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We
     * will create an operation and run it using wizard as execution context.
     */
    public boolean performFinish()
    {
        if (!RhodesProject.checkNature(m_currentProject) && !RhoelementsProject.checkNature(m_currentProject))
            return true;

        IRunnableWithProgress op = new IRunnableWithProgress()
        {
            public void run(IProgressMonitor monitor) throws InvocationTargetException
            {
                try
                {
                    doFinish(monitor);
                }
                catch (CoreException e)
                {
                    throw new InvocationTargetException(e);
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

    private void createProjectFiles(IProgressMonitor monitor) throws IOException
    {
        RakeTask task = new GenerateRhodesSpecTask(m_currentProject.getLocation().toOSString());
        Map<String, ?> results = task.run(monitor);

        if (TaskResultConverter.getResultIntCode(results) != 0)
        {
            throw new IOException("The RhoMobile SDK do not installed");
        }
    }

    /**
     * The worker method. It will find the container, create the file if missing
     * or just replace its contents, and open the editor on the newly created
     * file.
     */
    private void doFinish(IProgressMonitor monitor) throws CoreException
    {
        try
        {
            monitor.beginTask("Creating spec files", 2);
            monitor.worked(1);
            monitor.setTaskName("Create project...");

            createProjectFiles(monitor);

            if (CommonConstants.checkRhodesVersion)
            {
                monitor.setTaskName("Check RhoMobile version...");

                try
                {
                    if (!RunExeHelper.checkRhodesVersion(CommonConstants.rhodesVersion))
                    {
                        throw new IOException();
                    }
                }
                catch (IOException e)
                {
                    String message = "Installed RhoMobile have old version, need RhoMobile version equal or greater "
                                            + CommonConstants.rhodesVersion
                                            + " Please reinstall it (See 'http://docs.rhomobile.com/rhodes/install' for more information)";
                    DialogUtils.error("Error", message);
                    return;
                }
            }

            m_currentProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);

            ShowPerspectiveJob job = new ShowPerspectiveJob("show RhoStudio perspective", UiConstants.rhodesPerspectiveId);
            job.schedule();

            monitor.worked(1);
        }
        catch (IOException e)
        {
            String message = "Cannot find RhoMobile, need RhoMobile version equal or greater "
                                    + CommonConstants.rhodesVersion
                                    + " (See 'http://docs.rhomobile.com/rhodes/install' for more information)";
            DialogUtils.error("Error", message);
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
}
