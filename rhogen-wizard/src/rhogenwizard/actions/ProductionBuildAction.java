package rhogenwizard.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import rhogenwizard.DialogUtils;
import rhogenwizard.PlatformType;
import rhogenwizard.builder.rhodes.SelectPlatformDialog;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;
import rhogenwizard.rhohub.TokenChecker;
import rhogenwizard.sdk.task.BuildPlatformTask;
import rhogenwizard.sdk.task.RunTask;

public class ProductionBuildAction implements IWorkbenchWindowActionDelegate
{
    private IWorkbenchWindow window;

    @Override
    public void run(IAction action)
    {
        IProject project = ProjectFactory.getInstance().getSelectedProject();

        if (project == null)
        {
            DialogUtils.error("Error", "Before run production build select RhoMobile project");
            return;
        }

		if (!TokenChecker.processToken(project.getLocation().toOSString()))
			return;

        if (!RhodesProject.checkNature(project) && !RhoelementsProject.checkNature(project))
        {
            DialogUtils.error("Error", "Production build can run only for RhoMobile project");
            return;
        }

        SelectPlatformDialog selectDlg = new SelectPlatformDialog(window.getShell());
        selectDlg.create();
         
        if (selectDlg.open() == Window.OK)
        {
        	PlatformType selectPlatform = selectDlg.getSelectedPlatform();
        	
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

                RunTask task = new BuildPlatformTask(project.getLocation().toOSString(), selectPlatform);
                task.makeJob("Production build (" + projectName + ")").schedule();
            }
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection)
    {
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void init(IWorkbenchWindow window)
    {
        this.window = window;
    }
}
