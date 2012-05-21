package rhogenwizard.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.sdk.facade.RhoTaskHolder;
import rhogenwizard.sdk.task.GenerateRhodesAppTask;
import rhogenwizard.sdk.task.GenerateRhodesSpecTask;

public class GenerateSpecAction implements IWorkbenchWindowActionDelegate 
{
	private IWorkbenchWindow m_workbenchWindow;
	/**
	 * The constructor.
	 */
	public GenerateSpecAction() 
	{
	}

	public void run(IAction action) 
	{
		IProject project = ProjectFactory.getInstance().getSelectedProject();
		
		if (project == null)
		{
			MessageDialog.openError(m_workbenchWindow.getShell(), "Error", "You can generate spec files after select rhodes project.");
			return;
		}
		
		if (!RhodesProject.checkNature(project))
		{
			MessageDialog.openInformation(m_workbenchWindow.getShell(), "Information", "Selected item is not rhodes project.");
			return;
		}
	
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(GenerateRhodesSpecTask.workDir, project.getLocation().toOSString());
		
		RhoTaskHolder.getInstance().runTask(GenerateRhodesSpecTask.class, params);
		
		try 
		{
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
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
		this.m_workbenchWindow = window;
	}
}