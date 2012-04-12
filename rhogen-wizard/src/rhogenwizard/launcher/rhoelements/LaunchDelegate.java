package rhogenwizard.launcher.rhoelements;

import java.io.FileNotFoundException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.constants.MsgConstants;

class LicMessageJob extends UIJob 
{
	private int retValue = -1;

	public LicMessageJob(String name) 
	{
		super(name);
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor)
	{
		Shell windowShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		String[] a = {"OK", "Cancel"};
		
		MessageDialog messageBox = new MessageDialog(windowShell, "Warrning", null, 
				MsgConstants.rhoelementsWarrningLicense, MessageDialog.WARNING,  a, 0);
		retValue = messageBox.open();	
		
		return Status.OK_STATUS;
	}
	
	public int getResultValue()
	{
		return retValue;
	}
}

public class LaunchDelegate extends rhogenwizard.launcher.rhodes.LaunchDelegate 
{	
	@Override
	public synchronized void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException 
	{
		setupConfigAttributes(configuration);

		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(m_projectName);
		
		try 
		{
			AppYmlFile ymlFile = AppYmlFile.createFromProject(project);
			
			if (!ymlFile.isRhoelements())
			{
				LicMessageJob msgJob = new LicMessageJob("");
				msgJob.schedule();
				msgJob.join();
				
				if (msgJob.getResultValue() == 0) // 0 is Ok button
				{
					ymlFile.enableRhoelementsFlag();
					ymlFile.save();
				}
			}
		}
		catch (FileNotFoundException e) 
		{
			//TODO - add error messages
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		super.launchProject(configuration, mode, launch, monitor);
	}
}