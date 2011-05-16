package rhogenwizard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.progress.UIJob;

public class ShowMessageJob extends UIJob 
{
	String m_titleText = null;
	String m_msgText = null;
	
	public ShowMessageJob(String name, String title, String msg) 
	{
		super(name);
		m_titleText = title;
		m_msgText   = msg;
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor)
	{
		Shell windowShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		MessageBox messageBox = new MessageBox(windowShell, SWT.ICON_WARNING | SWT.OK);
		messageBox.setText(m_titleText);
		messageBox.setMessage(m_msgText);
		messageBox.open();	
		
		return new Status(BUILD, Activator.PLUGIN_ID, "show message");
	}
}