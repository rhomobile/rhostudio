package rhogenwizard.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import rhogenwizard.wizards.ModelWizard;

public class NewModelAction implements IObjectActionDelegate 
{
	private Shell   m_shell = null;
	private IProject m_currentProject = null;
	
	/**
	 * Constructor for Action1.
	 */
	public NewModelAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) 
	{
		m_shell = targetPart.getSite().getShell();
		
		m_currentProject = getCurrentProjectLocation(targetPart);
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) 
	{
		if (null != m_currentProject)
		{
			ModelWizard wizard = new ModelWizard(m_currentProject);
			
			WizardDialog dialog = new WizardDialog(m_shell, wizard);
			dialog.create();
			dialog.open();
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}
	
	private IProject getCurrentProjectLocation(IWorkbenchPart targetPart)
	{
		try
		{
			IWorkbenchWindow worbenchWindow = targetPart.getSite().getWorkbenchWindow();
			
			ISelectionService selService = worbenchWindow.getSelectionService();
			
			ITreeSelection currentSelection = (ITreeSelection)selService.getSelection();
			
			if (null != currentSelection)
			{
				org.eclipse.jface.viewers.TreePath treeItemSelections = currentSelection.getPaths()[0];
				
				IProject currentProject = (IProject) treeItemSelections.getSegment(0);
						
				return currentProject;
			}
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
