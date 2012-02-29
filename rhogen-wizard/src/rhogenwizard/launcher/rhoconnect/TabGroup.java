package rhogenwizard.launcher.rhoconnect;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab; 

public class TabGroup extends AbstractLaunchConfigurationTabGroup 
{
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) 
	{
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] 
		{
			new ParametersTab()
		};
		
		setTabs(tabs);
	}

}
