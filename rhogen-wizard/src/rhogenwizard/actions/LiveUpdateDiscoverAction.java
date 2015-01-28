package rhogenwizard.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import rhogenwizard.builder.rhodes.ConfigProductionBuildDialog;

public class LiveUpdateDiscoverAction implements IWorkbenchWindowActionDelegate 
{
    private IWorkbenchWindow window;

	@Override
	public void run(IAction action) 
	{
        ConfigProductionBuildDialog dialog = new ConfigProductionBuildDialog(window.getShell());
        dialog.create();
         
        if (dialog.open() == Window.OK)
        {
        	
        }
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window)
	{
		this.window = window;
	}
}
