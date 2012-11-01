package rhogenwizard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.progress.UIJob;

public class ShowOnlyHidePerspectiveJob extends UIJob 
{
	String m_perspectiveId = null;
	
	public ShowOnlyHidePerspectiveJob(String name, String id) 
	{
		super(name);
		
		m_perspectiveId = id;
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor)
	{
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		
		if (windows.length > 0) 
		{
			IWorkbenchWindow mainWindow        = windows[0];
			IWorkbenchPage[] wbPages           = (IWorkbenchPage[]) mainWindow.getPages();
			String           currPerspectiveId = null;
			
			if (wbPages.length > 0)
			{
				currPerspectiveId = wbPages[0].getPerspective().getId();
			}

			if (currPerspectiveId.equals(m_perspectiveId))
				return Status.CANCEL_STATUS;
			
			try 
			{
				PlatformUI.getWorkbench().showPerspective(m_perspectiveId, windows[0]);
			}
			catch (WorkbenchException e) 
			{
				e.printStackTrace();
			}
		}
		
		return Status.OK_STATUS;
	}

}
