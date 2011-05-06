package rhogenwizard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.progress.UIJob;

import rhogenwizard.debugger.RhogenConstants;

public class ShowPerspectiveJob extends UIJob
{
	String m_perspectiveId = null;
	
	public ShowPerspectiveJob(String name, String id) 
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
			try 
			{
				PlatformUI.getWorkbench().showPerspective(m_perspectiveId, windows[0]);
			}
			catch (WorkbenchException e) 
			{
				e.printStackTrace();
			}
		}
		
		return new Status(BUILD, Activator.PLUGIN_ID, "chnage perspective");
	}
}