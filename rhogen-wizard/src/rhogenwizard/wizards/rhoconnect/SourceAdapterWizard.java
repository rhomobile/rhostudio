package rhogenwizard.wizards.rhoconnect;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import rhogenwizard.DialogUtils;
import rhogenwizard.ShowPerspectiveJob;
import rhogenwizard.constants.MsgConstants;
import rhogenwizard.constants.UiConstants;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhoconnectProject;
import rhogenwizard.sdk.facade.RhoTaskHolder;
import rhogenwizard.sdk.helper.TaskResultConverter;
import rhogenwizard.sdk.task.GenerateRhoconnectAdapterTask;
import rhogenwizard.sdk.task.RakeTask;
import rhogenwizard.wizards.ZeroPage;

public class SourceAdapterWizard extends Wizard implements INewWizard
{
    private static final String okRhodesVersionFlag = "1";

    private SourceAdapterWizardPage m_pageApp = null;
    private ISelection m_selection = null;
    private IProject m_currentProject = null;
    private String m_projectLocation = null;

    /**
     * Constructor for SampleNewWizard.
     */
    public SourceAdapterWizard()
    {
        super();
        setNeedsProgressMonitor(true);

        m_currentProject = ProjectFactory.getInstance().getSelectedProject();

        if (m_currentProject != null)
        {
            m_projectLocation = m_currentProject.getLocation().toOSString();
        }
    }

    /**
     * Adding the page to the wizard.
     */
    public void addPages()
    {
        if (m_currentProject != null)
        {
            if (!RhoconnectProject.checkNature(m_currentProject))
            {
                ZeroPage zeroPage =
                        new ZeroPage("Project " + m_currentProject.getName()
                                + " is not rhoconnect application.");
                addPage(zeroPage);
            }
            else
            {
                m_pageApp = new SourceAdapterWizardPage(m_selection);
                addPage(m_pageApp);
            }
        }
        else
        {
            ZeroPage zeroPage =
                    new ZeroPage("Select rhoconnect project for create source adapter.");
            addPage(zeroPage);
        }
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We
     * will create an operation and run it using wizard as execution context.
     */
    public boolean performFinish()
    {
        if (!RhoconnectProject.checkNature(m_currentProject))
            return true;

        final String srcAdapterName = m_pageApp.getAdapterName();

        IRunnableWithProgress op = new IRunnableWithProgress()
        {
            public void run(IProgressMonitor monitor) throws InvocationTargetException
            {
                try
                {
                    doFinish(srcAdapterName, monitor);
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

    /**
     * The worker method. It will find the container, create the file if missing
     * or just replace its contents, and open the editor on the newly created
     * file.
     */
    private void doFinish(String adapterName, IProgressMonitor monitor) throws CoreException
    {
        IProject newProject = null;

        try
        {
            if (m_currentProject.isOpen())
            {
                monitor.beginTask("Creating " + m_currentProject.getName(), 2);
                monitor.worked(1);
                monitor.setTaskName("Opening file for editing...");

                RakeTask task =
                        new GenerateRhoconnectAdapterTask(m_projectLocation, adapterName);
                Map<String, ?> results = task.run(monitor);

                if (TaskResultConverter.getResultIntCode(results) != 0)
                {
                    throw new IOException("The Rhodes SDK do not installed");
                }

                m_currentProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);

                ShowPerspectiveJob job =
                        new ShowPerspectiveJob("show rhomobile perspective",
                                UiConstants.rhodesPerspectiveId);
                job.schedule();
            }

            monitor.worked(1);
        }
        catch (IOException e)
        {
            DialogUtils.error("Error", MsgConstants.errFindRhosync);
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