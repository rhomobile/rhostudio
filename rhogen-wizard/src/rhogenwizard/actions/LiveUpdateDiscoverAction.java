package rhogenwizard.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import rhogenwizard.DialogUtils;
import rhogenwizard.builder.rhodes.ConfigProductionBuildDialog;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;
import rhogenwizard.rhohub.TokenChecker;

public class LiveUpdateDiscoverAction implements IWorkbenchWindowActionDelegate 
{
    private IWorkbenchWindow window;

	@Override
	public void run(IAction action) 
	{
		IProject project = ProjectFactory.getInstance().getSelectedProject();

        if (project == null)
        {
            DialogUtils.error("Error", "Before run live update discover select RhoMobile project's");
            return;
        }

        if (!RhodesProject.checkNature(project) && !RhoelementsProject.checkNature(project))
        {
            DialogUtils.error("Error", "Live update feature can run only for RhoMobile project's");
            return;
        }

        if (!TokenChecker.processToken(project))
            return;
        
        LiveUpdateDiscoverDialog dialog = new LiveUpdateDiscoverDialog(project, window.getShell());
        
        dialog.create();
          
        if (dialog.open() == Window.OK)
        {
        	
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
