package rhogenwizard.launcher;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
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

import rhogenwizard.OSHelper;
import rhogenwizard.RhodesAdapter;
import rhogenwizard.RhodesProjectSupport;
import rhogenwizard.ShowMessageJob;
import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.buildfile.SdkYmlFile;
import rhogenwizard.constants.ConfigurationConstants;

public class RhosyncParametersTab extends  JavaLaunchTab 
{
	private static int    minTabSize      = 650;
	
	private static String bbVersions[] = {};
	
	Composite 	m_comp = null;
	Combo 	  	m_selectPlatformCombo = null;
	Combo       m_selectPlatformVersionCombo = null;
	Text 		m_appNameText = null;

	String    	m_platformName = null;
	IProject 	m_selProject  = null;
	
	ILaunchConfigurationWorkingCopy m_configuration;
	
	@SuppressWarnings("restriction")
	@Override
	public void createControl(final Composite parent)
	{
		Composite composite = SWTFactory.createComposite(parent, 1, 1, GridData.FILL_HORIZONTAL);
		m_comp = composite;
		
		Composite namecomp = SWTFactory.createComposite(composite, composite.getFont(), 3, 1, GridData.FILL_HORIZONTAL, 0, 0);
		
		// 1 row
		Label label = SWTFactory.createLabel(namecomp, "&Project name:", 1);

		m_appNameText = SWTFactory.createText(namecomp, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY, 1);	
		m_appNameText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				if (m_configuration != null)
				{
					m_configuration.setAttribute(ConfigurationConstants.projectNameCfgAttribute, m_appNameText.getText());
					showApplyButton();
				}
			}
		});
			
		Button browseButton = SWTFactory.createPushButton(namecomp, "Browse...", null);
		browseButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				selectProjectDialog();
			}
		});
	
	}
	
	@Override
	public Control getControl() 
	{
		return m_comp;
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		m_configuration = configuration;
	}
	
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) 
	{
		m_configuration = configuration;
		
		if (m_selProject == null)
		{
			m_selProject = RhodesProjectSupport.getSelectedProject();

			if (m_selProject == null)
			{
				IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				
				if (allProjects.length > 0)
				{
					m_selProject = allProjects[0];
				}
			}
		}
		
		if (m_selProject != null)
		{
			configuration.setAttribute(ConfigurationConstants.projectNameCfgAttribute, m_selProject.getName());
		}				
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) 
	{
		try 
		{
			Control scrollParent = getLaunchConfigurationDialog().getActiveTab().getControl().getParent();
			
			if (scrollParent instanceof ScrolledComposite)
			{
				ScrolledComposite sc = (ScrolledComposite)scrollParent;
				sc.setMinSize(scrollParent.computeSize(minTabSize, SWT.DEFAULT));	
			}
			
			String selProjectName = configuration.getAttribute(ConfigurationConstants.projectNameCfgAttribute, "");
			
			m_selProject = ResourcesPlugin.getWorkspace().getRoot().getProject(selProjectName);	
			
			m_appNameText.setText(selProjectName);
		}
		catch (CoreException e) 
		{
			e.printStackTrace();
		} 
	}
	
	@Override
	public boolean canSave()
	{
		return true;
	}

	@Override
	public String getName() 
	{
		return "Common setting";
	}
	
	void selectProjectDialog()
	{
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
					getShell(), ResourcesPlugin.getWorkspace().getRoot(), false, "Select project");
			
		if (dialog.open() == ContainerSelectionDialog.OK) 
		{
			Object[] result = dialog.getResult();
			
			if (result.length == 1) 
			{				
				String selProjectName = ((Path) result[0]).toString();
				selProjectName = selProjectName.replaceAll("/", "");
				
				m_selProject = ResourcesPlugin.getWorkspace().getRoot().getProject(selProjectName);
				m_appNameText.setText(selProjectName);
				
				m_configuration.setAttribute(ConfigurationConstants.projectNameCfgAttribute, m_selProject.getName());
								
				showApplyButton();
			}
			else
			{
				MessageDialog.openInformation(getShell(), "Message", "Select single project.");
			}
		}
	}
	
	private void showApplyButton()
	{
		this.setDirty(false);
		this.getLaunchConfigurationDialog().updateButtons();		
	}
}
