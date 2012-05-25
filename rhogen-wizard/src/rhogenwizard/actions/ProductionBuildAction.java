package rhogenwizard.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import rhogenwizard.DialogUtils;
import rhogenwizard.PlatformType;
import rhogenwizard.builder.rhodes.SelectPlatformDialog;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;
import rhogenwizard.sdk.task.BuildPlatformTask;
import rhogenwizard.sdk.task.RakeTask;

public class ProductionBuildAction implements IWorkbenchWindowActionDelegate
{
    private IWorkbenchWindow window;

    /**
     * The action has been activated. The argument of the method represents the
     * 'real' action sitting in the workbench UI.
     * 
     * @see IWorkbenchWindowActionDelegate#run
     */
    public void run(IAction action)
    {
        IProject project = ProjectFactory.getInstance().getSelectedProject();

        if (project == null)
        {
            DialogUtils.error("Error", "Before run production build select RhoMobile project");
            return;
        }

        if (!RhodesProject.checkNature(project) && !RhoelementsProject.checkNature(project))
        {
            DialogUtils.error("Error", "Production build can run only for RhoMobile project");
            return;
        }

        SelectPlatformDialog selectDlg = new SelectPlatformDialog(window.getShell());
        PlatformType selectPlatform = selectDlg.open();

        if (selectPlatform != PlatformType.eUnknown)
        {
            String projectName = "<unknown>";
            try
            {
                projectName = project.getDescription().getName();
            }
            catch (CoreException e)
            {
            }

            RakeTask task = new BuildPlatformTask(project.getLocation().toOSString(), selectPlatform);
            task.makeJob("Production build (" + projectName + ")").schedule();
        }
    }

    /**
     * Selection in the workbench has been changed. We can change the state of
     * the 'real' action here if we want, but this can only happen after the
     * delegate has been created.
     * 
     * @see IWorkbenchWindowActionDelegate#selectionChanged
     */
    public void selectionChanged(IAction action, ISelection selection)
    {
    }

    /**
     * We can use this method to dispose of any system resources we previously
     * allocated.
     * 
     * @see IWorkbenchWindowActionDelegate#dispose
     */
    public void dispose()
    {
    }

    /**
     * We will cache window object in order to be able to provide parent shell
     * for the message dialog.
     * 
     * @see IWorkbenchWindowActionDelegate#init
     */
    public void init(IWorkbenchWindow window)
    {
        this.window = window;
    }
}
