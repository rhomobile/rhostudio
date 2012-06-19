package rhogenwizard.launcher.spec;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.dialogs.MessageDialog;


public class ParametersTab extends rhogenwizard.launcher.rhodes.ParametersTab 
{
	private boolean checkSpecFiles()
	{
		IProject selProject = getSelectProject();
		
		File specFile = new File(selProject.getLocation().toOSString() + "/app/spec_runner.rb");
		
		return specFile.exists();
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) 
	{
		super.initializeFrom(configuration);
		
		if (!checkSpecFiles())
		{
			MessageDialog.openError(getShell(), "Error", "Spec files not found in project " + getSelectProject().getName());
		}
	}
}
