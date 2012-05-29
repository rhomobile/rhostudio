package rhogenwizard.wizards.rhodes;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

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
import rhogenwizard.OSHelper;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;
import rhogenwizard.sdk.task.GenerateRhodesModelTask;
import rhogenwizard.sdk.task.RunTask;
import rhogenwizard.wizards.ZeroPage;

public class ModelWizard extends Wizard implements INewWizard
{
    private ModelWizardPage m_pageModel = null;
    private ISelection m_selection = null;
    private IProject m_currentProject = null;

    public ModelWizard()
    {
        this(ProjectFactory.getInstance().getSelectedProject());
    }

    public ModelWizard(IProject currentProject)
    {
        setNeedsProgressMonitor(true);
        m_currentProject = currentProject;
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
                m_pageModel = new ModelWizardPage(m_selection);
                addPage(m_pageModel);
            }
        }
        else
        {
            ZeroPage zeroPage = new ZeroPage("Select RhoMobile project for create model");
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

        final String modelName = m_pageModel.getModelName();
        final String modelParams = m_pageModel.getModelParams();

        IRunnableWithProgress op = new IRunnableWithProgress()
        {
            public void run(IProgressMonitor monitor) throws InvocationTargetException
            {
                try
                {
                    doFinish(modelName, modelParams, monitor);
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
    private void doFinish(String modelName, String modelParams, IProgressMonitor monitor) throws CoreException
    {
        try
        {
            monitor.beginTask("Creating model " + modelName, 2);
            monitor.worked(1);
            monitor.setTaskName("Creating model...");

            String projectLocation = (m_currentProject == null) ? null : m_currentProject.getLocation().toOSString();

            if (null != projectLocation)
            {
                createModel(monitor, projectLocation, modelName, modelParams, m_currentProject);
            }
            else
            {
                // TODO show error message
            }

            monitor.worked(1);
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

    private static void createModel(IProgressMonitor monitor, String projectLocation,
            String modelName, String modelParams, IProject currentProject)
    {
        File modelFolder = new File(projectLocation + "/app/" + modelName);

        if (modelFolder.exists())
        {
            boolean ok = DialogUtils.confirm(
            	"Model create", "Model with name " + modelName + " was already created. Delete old model?");
            
            if (!ok)
            {
                return;
            }
            
            OSHelper.deleteFolder(modelFolder);
        }

        try
        {
            RunTask task = new GenerateRhodesModelTask(projectLocation, modelName, modelParams);
            task.run(monitor);

            if (!task.isOk())
            {
                throw new IOException("The Rhodes SDK do not installed");
            }

            currentProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
