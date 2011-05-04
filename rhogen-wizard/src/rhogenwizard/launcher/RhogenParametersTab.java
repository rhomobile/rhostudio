package rhogenwizard.launcher;

import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import rhogenwizard.RhodesAdapter;
import rhogenwizard.RhodesProjectSupport;
import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.buildfile.SdkYmlFile;

public class RhogenParametersTab extends  JavaLaunchTab  //AbstractLaunchConfigurationTab
{
	private static int    minTabSize      = 650;
	private static String platformItems[] = {  "Android simulator", 
									           "Android phone", 
									           "iPhone simulator", 
									           "iPhone phone",
									           "Windows Mobile simulator",
									           "Windows Mobile phone",
									           "Blackberry simulator",
									           "Blackberry phone" };
	
	private static String androidVersions[] = { "1.6",
											    "2.1",
											    "2.2",
											    "2.3.1",
											    "2.3.3",
											    "3.0" };
	
	private static String bbVersions[] = {};
	
	Composite 	m_comp = null;
	Combo 	  	m_selectPlatformCombo = null;
	Combo       m_selectPlatformVersionCombo = null;
	Text 		m_appNameText = null;
	Text 		m_appLogText = null;
	Text        m_adroidEmuNameText = null;
	Button 		m_cleanButton = null;
	Label       m_androidEmuNameLabel = null;
	
	String    	m_platformName = null;
	IProject 	m_selProject  = null;
	String      m_selPlatformVersion = null;
	
	ILaunchConfigurationWorkingCopy m_configuration;
	AppYmlFile m_ymlFile = null;
	
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
					m_configuration.setAttribute(RhogenLaunchDelegate.projectNameCfgAttribute, m_appNameText.getText());
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
					showApplyButton();
				}
			}
		});
		
        GridData comboAligment = new GridData();
        comboAligment.horizontalAlignment = GridData.FILL;
        
		m_selectPlatformVersionCombo = SWTFactory.createCombo(namecomp, SWT.READ_ONLY, 1, androidVersions);
		m_selectPlatformVersionCombo.setLayoutData(comboAligment);
		m_selectPlatformVersionCombo.addSelectionListener(new SelectionAdapter()
		{	
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if (m_configuration != null)
				{
					encodeVersionCombo(m_selectPlatformVersionCombo.getText());
					showApplyButton();
				}
			}
		});
		m_selectPlatformVersionCombo.select(2);
		
		// 3 row
		m_androidEmuNameLabel = SWTFactory.createLabel(namecomp, "AVD name", 1);
		
		m_adroidEmuNameText = SWTFactory.createText(namecomp, SWT.BORDER | SWT.SINGLE, 1);
		m_adroidEmuNameText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				if (m_configuration != null && m_ymlFile != null)
				{
					try 
					{
						m_configuration.setAttribute(RhogenLaunchDelegate.androidEmuNameAttribute, m_adroidEmuNameText.getText());
						m_ymlFile.setAndroidEmuName(m_adroidEmuNameText.getText());
						m_ymlFile.save();

						showApplyButton();
					} 
					catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		// 4 row
        GridData checkBoxAligment = new GridData();
        checkBoxAligment.horizontalAlignment = GridData.FILL;
        checkBoxAligment.horizontalSpan = 3;
        
		m_cleanButton = new Button(composite, SWT.CHECK);
		m_cleanButton.setText("Clean before build");
		m_cleanButton.setSelection(false);
		m_cleanButton.setLayoutData(checkBoxAligment);
		m_cleanButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (m_configuration != null)
				{
					m_configuration.setAttribute(RhogenLaunchDelegate.isCleanAttribute, m_cleanButton.getSelection());
					showApplyButton();
				}
			}
		});
	}
	
	private void showAndroidEmuName(boolean isVisible)
	{
		m_androidEmuNameLabel.setVisible(isVisible);
		m_adroidEmuNameText.setVisible(isVisible);
	}

	protected void changeAdroidEmuName(String newName) 
	{
		if (m_ymlFile != null)
		{
			m_ymlFile.setAndroidEmuName(newName);
		}
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

	private void setAndroidEmuName()
	{
		try 
		{
			showAndroidEmuName(false);
			
			String selProjectPlatform = m_configuration.getAttribute(RhogenLaunchDelegate.platforrmCfgAttribute, "");
			String emuName            = m_configuration.getAttribute(RhogenLaunchDelegate.androidEmuNameAttribute, "");
			boolean onDevice          = m_configuration.getAttribute(RhogenLaunchDelegate.platforrmDeviceCfgAttribute, false);
			
			if (!onDevice && selProjectPlatform.equals(RhodesAdapter.platformAdroid))
			{
				showAndroidEmuName(true);
				
				m_adroidEmuNameText.setText(emuName);
			}
		}
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
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
			configuration.setAttribute(RhogenLaunchDelegate.projectNameCfgAttribute, m_selProject.getName());
			
			try 
			{
				m_ymlFile = AppYmlFile.createFromProject(m_selProject);
			
				String androidVersion = m_ymlFile.getAndroidVer();
				String bbVersion      = m_ymlFile.getBlackberryVer();
				String androidEmuName = m_ymlFile.getAndroidEmuName();
				
				configuration.setAttribute(RhogenLaunchDelegate.androidVersionAttribute, androidVersion);
				configuration.setAttribute(RhogenLaunchDelegate.blackberryVersionAttribute, bbVersion);
				configuration.setAttribute(RhogenLaunchDelegate.androidEmuNameAttribute, androidEmuName);
			} 
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		
		configuration.setAttribute(RhogenLaunchDelegate.platforrmCfgAttribute, (String) RhodesAdapter.platformAdroid);
		configuration.setAttribute(RhogenLaunchDelegate.platforrmDeviceCfgAttribute, false);
		configuration.setAttribute(RhogenLaunchDelegate.isCleanAttribute, false);
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
			
			String selProjectName = null, selProjectPlatform = null, selAndroidEmuName = null;
			selProjectName        = configuration.getAttribute(RhogenLaunchDelegate.projectNameCfgAttribute, "");
			selProjectPlatform    = configuration.getAttribute(RhogenLaunchDelegate.platforrmCfgAttribute, "");
			boolean onDevice      = configuration.getAttribute(RhogenLaunchDelegate.platforrmDeviceCfgAttribute, false);
			boolean isClean       = configuration.getAttribute(RhogenLaunchDelegate.isCleanAttribute, false);
			selAndroidEmuName     = configuration.getAttribute(RhogenLaunchDelegate.androidEmuNameAttribute, "");
			
			if (selProjectName != "")
			{
				m_selProject = ResourcesPlugin.getWorkspace().getRoot().getProject(selProjectName);
				m_appNameText.setText(selProjectName);
				m_adroidEmuNameText.setText(selAndroidEmuName);
				
				if (m_selProject.isOpen()) {
					m_ymlFile = AppYmlFile.createFromProject(m_selProject);
				}
			}
			
			setPlatformCombo(selProjectPlatform, onDevice);
			
			m_cleanButton.setSelection(isClean);
		}
		catch (CoreException e) 
		{
			e.printStackTrace();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
	}
	
	private void setPlatformVersionCombo() 
	{
		try
		{
			int maxAndroidVerIdx = androidVersions.length - 1;
		
			String selProjectPlatform = m_configuration.getAttribute(RhogenLaunchDelegate.platforrmCfgAttribute, "");
			String selAndroidVer      = m_configuration.getAttribute(RhogenLaunchDelegate.androidVersionAttribute, androidVersions[maxAndroidVerIdx]);
			String selBlackBarryVer   = m_configuration.getAttribute(RhogenLaunchDelegate.blackberryVersionAttribute, "");
			String selAndroidEmuName  = m_configuration.getAttribute(RhogenLaunchDelegate.androidEmuNameAttribute, "");
			boolean onDevice          = m_configuration.getAttribute(RhogenLaunchDelegate.platforrmDeviceCfgAttribute, false);
			
			if (selProjectPlatform.equals(RhodesAdapter.platformAdroid))
			{
				if (onDevice)
				{
					showVersionCombo(false);
					showAndroidEmuName(false);
				}else
				{
					showAndroidVersions();
					showVersionCombo(true);
					showAndroidEmuName(true);
					setAndroidEmuName();
					
					for (int idx=0; idx < androidVersions.length; idx++)
					{
						String currVer = androidVersions[idx];
						
						if (currVer.equals(selAndroidVer))
						{
							m_selectPlatformVersionCombo.select(idx);
							break;
						}
					}
				}
			}
			else if (selProjectPlatform.equals(RhodesAdapter.platformBlackBerry))
			{
				List<String> bbVersions = showBbVersions();
				showVersionCombo(true);
				
				for (int idx=0; idx < bbVersions.size(); idx++)
				{
					String currVer = bbVersions.get(idx);
					
					if (currVer.equals(selBlackBarryVer))
					{
						m_selectPlatformVersionCombo.select(idx);
						break;
					}
				}
			}
			else
			{
				showVersionCombo(false);
			}
		}
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
	}

	private void setPlatformCombo(String selProjectPlatform, boolean onDevice)
	{
		int platformIdx = -1;
		
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
				
				try 
				{
					m_ymlFile = AppYmlFile.createFromProject(m_selProject);
					setPlatformVersionCombo();
				}
				catch (FileNotFoundException e) 
				{
					e.printStackTrace();
				}
				
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
	
	void showVersionCombo(boolean isShow)
	{
		if (m_selectPlatformVersionCombo != null)
		{
			m_selectPlatformVersionCombo.setEnabled(isShow);
			m_selectPlatformVersionCombo.setVisible(isShow);
		}
	}
	
	void encodeVersionCombo(String selVersion)
	{
		try
		{
			String androidVersion = m_ymlFile.getAndroidVer();
			String bbVersion      = m_ymlFile.getBlackberryVer();
			
			String selPlatform = m_configuration.getAttribute(RhogenLaunchDelegate.platforrmCfgAttribute, "");
			
			RhodesAdapter.EPlatformType type = RhodesAdapter.convertPlatformFromDesc(selPlatform);
			
			if (type == RhodesAdapter.EPlatformType.eAndroid)
			{
				m_configuration.setAttribute(RhogenLaunchDelegate.androidVersionAttribute, selVersion);
				m_ymlFile.setAndroidVer(selVersion);
			}
			else if (type == RhodesAdapter.EPlatformType.eBb)
			{
				m_configuration.setAttribute(RhogenLaunchDelegate.blackberryVersionAttribute, selVersion);
				m_ymlFile.setBbVer(selVersion);
			}
			
			m_ymlFile.save();
		}
		catch(CoreException e)
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void showAndroidVersions()
	{
		m_selectPlatformVersionCombo.removeAll();
		
		for (String s: androidVersions) 
		{
			m_selectPlatformVersionCombo.add(s);
		}
	}
	
	private List<String> showBbVersions()
	{
		try 
		{
			m_selectPlatformVersionCombo.removeAll();
			
			String m_ymlSdkPath = m_ymlFile.getSdkConfigPath();
			
			SdkYmlFile sdkYmlFile = new SdkYmlFile(m_ymlSdkPath);
			
			List<String> bbVers = sdkYmlFile.getBbVersions();
			
			for (String s: bbVers) 
			{
				m_selectPlatformVersionCombo.add(s);
			}
			
			m_selectPlatformVersionCombo.select(0);
			
			return bbVers;
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void encodePlatformInformation(String selPlatform)
	{
		m_configuration.setAttribute(RhogenLaunchDelegate.platforrmDeviceCfgAttribute, selPlatform.contains("phone"));
		
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
		
		setPlatformVersionCombo();
		setAndroidEmuName();
	}
}
