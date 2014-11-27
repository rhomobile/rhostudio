package rhogenwizard.launcher.rhodes;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

public class LaunchDelegate extends rhogenwizard.launcher.LaunchDelegateBase
{
    public LaunchDelegate()
    {
        super(null, new String[0]);
    }

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException 
	{
		setupConfigAttributes(configuration);
		
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(m_projectName);
		
		super.launchLocalProject(project, configuration, mode, launch, monitor);
	}
}
