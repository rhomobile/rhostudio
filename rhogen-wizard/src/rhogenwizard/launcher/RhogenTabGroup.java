package rhogenwizard.launcher;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab; 

public class RhogenTabGroup extends AbstractLaunchConfigurationTabGroup 
{
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) 
	{
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] 
		{
			new RhogenParametersTab()
		};
		
		setTabs(tabs);
	}

}
