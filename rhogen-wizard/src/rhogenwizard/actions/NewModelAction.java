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

import rhogenwizard.wizards.RhogenModelWizard;

public class NewModelAction implements IObjectActionDelegate 
{
	private Shell   m_shell = null;
	private String  m_projectLocation = null;
	
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
		
		m_projectLocation = getCurrectProjectLocation(targetPart);
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) 
	{
		@SuppressWarnings("unused")
		RhogenModelWizard wizard = new RhogenModelWizard(m_projectLocation);
		
		WizardDialog dialog = new WizardDialog(m_shell, wizard );
		dialog.create();
		dialog.open();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}
	
	private String getCurrectProjectLocation(IWorkbenchPart targetPart)
	{
		try
		{
			IWorkbenchWindow worbenchWindow = targetPart.getSite().getWorkbenchWindow();
			
			ISelectionService selService = worbenchWindow.getSelectionService();
			
			ITreeSelection currectSelection = (ITreeSelection)selService.getSelection();
			
			if (null != currectSelection)
			{
				org.eclipse.jface.viewers.TreePath treeItemSelections = currectSelection.getPaths()[0];
				
				IProject currentProject = (IProject) treeItemSelections.getSegment(0);
						
				return currentProject.getLocation().toOSString();				
			}
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
