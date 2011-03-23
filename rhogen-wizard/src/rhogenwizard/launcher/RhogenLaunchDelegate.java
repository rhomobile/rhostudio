package rhogenwizard.launcher;

import java.io.File;
import java.security.KeyStore.Builder;
import java.util.Map;

import org.eclipse.core.internal.events.BuildManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.internal.ui.stringsubstitution.ResourceSelector;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.ResourceSelectionUtil;
import org.eclipse.ui.views.navigator.IResourceNavigator;
import org.eclipse.ui.views.navigator.ResourceNavigator;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.builder.RhogenBuilder;

public class RhogenLaunchDelegate extends LaunchConfigurationDelegate implements IDebugEventSetListener 
{		
	public static final String projectNameCfgAttribute = "project_name";
	public static final String platforrmCfgAttribute = "platform";
	
	private ILaunch m_launch = null;
	private String  m_projectName = null;
	private String  m_platformName = null;

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public synchronized void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException 
	{
		try
		{
			m_launch = launch;
	
			m_projectName  = configuration.getAttribute(projectNameCfgAttribute, "");
			m_platformName = configuration.getAttribute(platforrmCfgAttribute, "");
			
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(m_projectName);
			
			if (project == null || m_platformName == null || m_platformName.length() == 0)
			{
				throw new IllegalArgumentException();
			}
			
			buildForLaunch(configuration, mode, monitor);
		}
		catch(IllegalArgumentException e)
		{
			ConsoleHelper.consolePrint("Error - Platform and project name should be assigned");
		}
	}

	@Override
	protected IProject[] getBuildOrder(ILaunchConfiguration configuration, String mode) throws CoreException 
	{
		if (m_projectName != null) 
		{
			m_projectName = m_projectName.trim();
			
			if (m_projectName.length() > 0) 
			{
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(m_projectName);

				project.setSessionProperty(RhogenBuilder.getPlatformQualifier(), m_platformName);
				
				IProject[] findProjects = { project };
				
				return findProjects;
			}
		}

		return null;
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) 
	{
	}
}

