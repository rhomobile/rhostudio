package rhogenwizard.launcher.rhoconnect;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import rhogenwizard.RhodesConfigurationRO;
import rhogenwizard.RhodesConfigurationRW;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhoconnectProject;

@SuppressWarnings("restriction")
public class ParametersTab extends  JavaLaunchTab 
{
	private static int    minTabSize      = 650;
	
	Composite 	m_comp                       = null;
	Combo 	  	m_selectPlatformCombo        = null;
	Combo       m_selectPlatformVersionCombo = null;
	Text 		m_appNameText                = null;

	String    	m_platformName = null;
	IProject 	m_selProject  = null;
	
	ILaunchConfigurationWorkingCopy m_configuration;
	
	@Override
	public void createControl(final Composite parent)
	{
		Composite composite = SWTFactory.createComposite(parent, 1, 1, GridData.FILL_HORIZONTAL);
		m_comp = composite;
		
		Composite namecomp = SWTFactory.createComposite(composite, composite.getFont(), 3, 1, GridData.FILL_HORIZONTAL, 0, 0);
		
		// 1 row
		SWTFactory.createLabel(namecomp, "&Project name:", 1);

		m_appNameText = SWTFactory.createText(namecomp, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY, 1);	
		m_appNameText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				if (m_configuration != null)
				{
					new RhodesConfigurationRW(m_configuration).project(m_appNameText.getText());
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
			m_selProject = ProjectFactory.getInstance().getSelectedProject();
			
			if (m_selProject == null)
			{
				IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				
				for (IProject project : allProjects)
				{
					if (RhoconnectProject.checkNature(project))
					{
						m_selProject = project;	
					}
				}				
			}
			else
			{
				if (!RhoconnectProject.checkNature(m_selProject))
				{
					m_selProject = null;	
				}
			}
		}
		
		if (m_selProject == null)
		{
			MessageDialog.openInformation(getShell(), "Message", "Create and select rhoconnect project before create the configuration.");
		}
		else
		{
		    new RhodesConfigurationRW(configuration).project(m_selProject.getName());
		}				
	}

	@Override
    public void initializeFrom(ILaunchConfiguration configuration)
    {
        Control scrollParent = getLaunchConfigurationDialog().getActiveTab().getControl().getParent();

        if (scrollParent instanceof ScrolledComposite)
        {
            ((ScrolledComposite) scrollParent).setMinSize(
                scrollParent.computeSize(minTabSize, SWT.DEFAULT));
        }

        String selProjectName = new RhodesConfigurationRO(configuration).project();

        if (selProjectName == null || selProjectName.length() == 0)
            return;

        m_selProject = ResourcesPlugin.getWorkspace().getRoot().getProject(selProjectName);

        m_appNameText.setText(selProjectName);
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
				
				IProject selProject = ResourcesPlugin.getWorkspace().getRoot().getProject(selProjectName);
				
				if (!RhoconnectProject.checkNature(selProject))
				{
					MessageDialog.openError(getShell(), "Message", "Project " + selProject.getName() + " is not rhoconnect application");
					return;
				}
				
				m_selProject = selProject;
				m_appNameText.setText(selProjectName);
				
				new RhodesConfigurationRW(m_configuration).project(m_selProject.getName());
								
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
