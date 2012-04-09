package rhogenwizard.launcher.rhoelements;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import rhogenwizard.PlatformType;
import rhogenwizard.RunType;
import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.buildfile.SdkYmlFile;
import rhogenwizard.constants.ConfigurationConstants;
import rhogenwizard.launcher.SpecFileHelper;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;

public class ParametersTab extends rhogenwizard.launcher.rhodes.ParametersTab  
{
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) 
	{
		m_configuration = configuration;
		
		if (m_selProject == null)
		{
			m_selProject = ProjectFactory.getInstance().getSelectedProject();

			if (m_selProject == null)
			{
				IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				
				for (IProject project : allProjects)
				{
					if (RhoelementsProject.checkNature(project))
					{
						m_selProject = project;	
					}
				}
			}
			else
			{
				if (!RhoelementsProject.checkNature(m_selProject))
				{
					m_selProject = null;	
				}	
			}
		}
		
		if (m_selProject == null)
		{
			MessageDialog.openInformation(getShell(), "Message", "Create and select rhoelements project before create the configuration.");
		}
		else
		{
			configuration.setAttribute(ConfigurationConstants.projectNameCfgAttribute, m_selProject.getName());
			
			try 
			{
				m_ymlFile = AppYmlFile.createFromProject(m_selProject);
				
				if (m_ymlFile != null)
				{
					String androidVersion = m_ymlFile.getAndroidVer();
					String bbVersion      = m_ymlFile.getBlackberryVer();
					String androidEmuName = m_ymlFile.getAndroidEmuName();
					String iphoneVersion  = m_ymlFile.getIphoneVer();
	
					iphoneVersion = iphoneVersion == null ? iphoneVersions[0] : iphoneVersion;
					
					configuration.setAttribute(ConfigurationConstants.androidVersionAttribute, androidVersion);
					configuration.setAttribute(ConfigurationConstants.blackberryVersionAttribute, bbVersion);
					configuration.setAttribute(ConfigurationConstants.androidEmuNameAttribute, androidEmuName);
					configuration.setAttribute(ConfigurationConstants.iphoneVersionAttribute, iphoneVersion);
				}
				else
				{
					configuration.setAttribute(ConfigurationConstants.androidVersionAttribute, androidVersions[0]);
					configuration.setAttribute(ConfigurationConstants.blackberryVersionAttribute, "");
					configuration.setAttribute(ConfigurationConstants.androidEmuNameAttribute, "");
					configuration.setAttribute(ConfigurationConstants.iphoneVersionAttribute, iphoneVersions[0]);					
				}
			} 
			catch (FileNotFoundException e)
			{
				MessageDialog.openError(getShell(), "Error", "File build.yml not exists or corrupted. Project - " + getSelectProject().getName());
				e.printStackTrace();
			}
		}
				
		configuration.setAttribute(ConfigurationConstants.platforrmCfgAttribute, (String) PlatformType.platformAdroid);		
		configuration.setAttribute(ConfigurationConstants.isCleanAttribute, false);
		configuration.setAttribute(ConfigurationConstants.isReloadCodeAttribute, false);
		configuration.setAttribute(ConfigurationConstants.isTraceAttribute, false);	
		configuration.setAttribute(ConfigurationConstants.simulatorType, RunType.platformRhoSim);
	}
}
