package rhogenwizard.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.jface.dialogs.MessageDialog;

import rhogenwizard.builder.rhodes.SelectPlatformBuildJob;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;

public class ProductionBuildAction implements IWorkbenchWindowActionDelegate 
{
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public ProductionBuildAction() 
	{
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) 
	{
		IProject project = ProjectFactory.getInstance().getSelectedProject();
		
		if (project == null)
			return;
		
		if (!RhodesProject.checkNature(project))
			return;
			
		SelectPlatformBuildJob buildJob = new SelectPlatformBuildJob("select platform", project.getLocation().toOSString());
		buildJob.run(new NullProgressMonitor());
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) 
	{
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose()
	{
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) 
	{
		this.window = window;
	}
}