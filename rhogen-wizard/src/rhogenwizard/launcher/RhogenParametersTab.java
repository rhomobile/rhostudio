package rhogenwizard.launcher;

import java.io.FileNotFoundException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import rhogenwizard.RhodesAdapter;
import rhogenwizard.StringHelper;
import rhogenwizard.buildfile.AppYmlFile;

public class RhogenParametersTab extends  JavaLaunchTab  //AbstractLaunchConfigurationTab
{
	private static String platformItems[] = {  "Android simplator", 
									           "Android phone", 
									           "iPhone simulator", 
									           "iPhone phone",
									           "Windows Mobile simulator",
									           "Windows Mobile phone",
									           "Blackberry simulator",
									           "Blackberry phone" };
	
	Composite 	m_comp = null;
	Combo 	  	m_selectPlatformCombo = null;
	Text 		m_appNameText = null;
	Text 		m_appLogText = null;
	
	String    	m_platformName = null;
	IProject 	m_selProject  = null;
	
	ILaunchConfigurationWorkingCopy m_configuration;
	
	@Override
	public void createControl(Composite parent)
	{
		Composite composite = SWTFactory.createComposite(parent, 1, 1, GridData.FILL_HORIZONTAL);
		m_comp = composite;

		Composite namecomp = SWTFactory.createComposite(composite, composite.getFont(), 3, 1, GridData.FILL_HORIZONTAL, 0, 0);

		// 1 row
		Label label = SWTFactory.createLabel(namecomp, "&Project name:", 1);

		m_appNameText = SWTFactory.createText(namecomp, SWT.BORDER | SWT.SINGLE, 1);	
		m_appNameText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				showApplyButton();
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
		
		// 2 row
		SWTFactory.createLabel(namecomp, "Platform:", 1); 
		
		m_selectPlatformCombo = SWTFactory.createCombo(namecomp, SWT.READ_ONLY, 1, platformItems);
		m_selectPlatformCombo.addSelectionListener(new SelectionAdapter()
		{	
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if (m_configuration != null)
				{
					encodePlatformInformation(m_selectPlatformCombo.getText());
				}
				
				showApplyButton();
			}
		});
		
		label = SWTFactory.createLabel(namecomp, "", 1);
	}
	
	IProject getSelectedProject()
	{
		IProject project = null;
		
		IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
		
		if (workbenchWindows.length > 0)
		{
			IWorkbenchPage page = workbenchWindows[0].getActivePage(); 
		
			ISelection selection = page.getSelection();
	
			if (selection instanceof IStructuredSelection)
			{
				IStructuredSelection sel = (IStructuredSelection) selection;
				Object res = sel.getFirstElement();
				
				if (res instanceof IResource)
				{
				   project = ((IResource)res).getProject();
				}		
			}
		}
		
		return project;
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
			m_selProject = getSelectedProject();

			if (m_selProject == null)
			{
				IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				
				if (allProjects.length == 1)
				{
					m_selProject = allProjects[0];			
				}
			}
		}
		
		if (m_selProject != null)
		{
			configuration.setAttribute(RhogenLaunchDelegate.projectNameCfgAttribute, m_selProject.getName());
		}
		
		configuration.setAttribute(RhogenLaunchDelegate.platforrmCfgAttribute, (String) RhodesAdapter.platformAdroid);
		configuration.setAttribute(RhogenLaunchDelegate.platforrmDeviceCfgAttribute, (String) "no");
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) 
	{
		try 
		{			
			String selProjectName = null, selProjectPlatform = null;
			selProjectName        = configuration.getAttribute(RhogenLaunchDelegate.projectNameCfgAttribute, "");
			selProjectPlatform    = configuration.getAttribute(RhogenLaunchDelegate.platforrmCfgAttribute, "");
			boolean onDevice      = configuration.getAttribute(RhogenLaunchDelegate.platforrmDeviceCfgAttribute, "").equals("yes");
			
			if (selProjectName != "")
			{
				m_selProject = ResourcesPlugin.getWorkspace().getRoot().getProject(selProjectName);
				
				if (m_selProject != null)
				{
					int platformIdx = -1;
					selProjectName = m_selProject.getName();
					
					m_appNameText.setText(selProjectName);

					if (selProjectPlatform.equals(RhodesAdapter.platformAdroid))
					{
						platformIdx = 0;
					}
					else if (selProjectPlatform.equals(RhodesAdapter.platformIPhone))
					{
						platformIdx = 2;
					}
					else if (selProjectPlatform.equals(RhodesAdapter.platformWinMobile))
					{
						platformIdx = 4;
					}
					else if (selProjectPlatform.equals(RhodesAdapter.platformBlackBerry))
					{
						platformIdx = 6;
					}

					if (onDevice)
					{
						platformIdx += 1;
					}

					m_selectPlatformCombo.select(platformIdx);
				}
			}
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
				
				m_configuration.setAttribute(RhogenLaunchDelegate.projectNameCfgAttribute, m_selProject.getName());
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
	
	private void encodePlatformInformation(String selPlatform)
	{
		if (selPlatform.contains("phone"))
		{
			m_configuration.setAttribute(RhogenLaunchDelegate.platforrmDeviceCfgAttribute, "yes");
		}
		else
		{
			m_configuration.setAttribute(RhogenLaunchDelegate.platforrmDeviceCfgAttribute, "no");
		}
		
		if (selPlatform.equals(platformItems[0]) || selPlatform.equals(platformItems[1]))
		{
			m_configuration.setAttribute(RhogenLaunchDelegate.platforrmCfgAttribute, RhodesAdapter.platformAdroid);
		}
		else if (selPlatform.equals(platformItems[2]) || selPlatform.equals(platformItems[3]))
		{
			m_configuration.setAttribute(RhogenLaunchDelegate.platforrmCfgAttribute, RhodesAdapter.platformIPhone);
		}
		else if (selPlatform.equals(platformItems[4]) || selPlatform.equals(platformItems[5]))
		{
			m_configuration.setAttribute(RhogenLaunchDelegate.platforrmCfgAttribute, RhodesAdapter.platformWinMobile);
		}	
		else if (selPlatform.equals(platformItems[6]) || selPlatform.equals(platformItems[7]))
		{
			m_configuration.setAttribute(RhogenLaunchDelegate.platforrmCfgAttribute, RhodesAdapter.platformBlackBerry);
		}
	}
}
