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

import rhogenwizard.OSHelper;
import rhogenwizard.PlatformType;
import rhogenwizard.RhodesProjectSupport;
import rhogenwizard.RunType;
import rhogenwizard.ShowMessageJob;
import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.buildfile.SdkYmlFile;
import rhogenwizard.constants.ConfigurationConstants;

public class RhogenParametersTab extends  JavaLaunchTab 
{
	private static int    minTabSize      = 650;
		
	private static String platformItems[] = {  "Android", 
									           "iPhone", 
									           "Windows Mobile",
									           "Blackberry",
									           "Windows phone",
									           "Symbian" };
	
	private static String androidVersions[] = { "1.6",
											    "2.1",
											    "2.2",
											    "2.3.1",
											    "2.3.3",
											    "3.0",
											    "3.1",
											    "3.2", 
											    "4.0" };

	private static String iphoneVersions[] = { "iphone",
											   "ipad" };
															

	private static String simulatorTypes[] = { RunType.platformRhoSim,
		                                       RunType.platformSim,
		                                       RunType.platformDevice };

	
	private static String bbVersions[] = {};
	
	Composite 	m_comp = null;
	Combo 	  	m_selectPlatformCombo = null;
	Combo       m_selectPlatformVersionCombo = null;
	Text 		m_appNameText = null;
	Text 		m_appLogText = null;
	Text        m_adroidEmuNameText = null;
	Button 		m_cleanButton = null;
	Label       m_androidEmuNameLabel = null;
	Label       m_platformTypeLabel = null;
	Combo       m_platformTypeCombo = null;
	Button      m_reloadButton = null;
	Button      m_traceButton = null;	
	
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
		m_selectPlatformCombo.select(0);		
			
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
		m_selectPlatformVersionCombo.select(0);
		
		// 3 row
		m_platformTypeLabel = SWTFactory.createLabel(namecomp, "Simulator type:", 1);
		
		m_platformTypeCombo = SWTFactory.createCombo(namecomp, SWT.READ_ONLY, 1, simulatorTypes);
		m_platformTypeCombo.addSelectionListener(new SelectionAdapter()
		{	
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if (m_configuration != null)
				{
					encodePlatformTypeCombo(m_platformTypeCombo.getText());
					encodePlatformInformation(m_selectPlatformCombo.getText());
					showApplyButton();
				}
			}
		});
		m_selectPlatformVersionCombo.select(0);
		
		SWTFactory.createLabel(namecomp, "", 1);
		
		// 4 row
		m_androidEmuNameLabel = SWTFactory.createLabel(namecomp, "AVD name", 1);
		
		m_adroidEmuNameText = SWTFactory.createText(namecomp, SWT.BORDER | SWT.SINGLE, 1);
		m_adroidEmuNameText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				encodeEmuNameText(m_adroidEmuNameText.getText());
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
					m_configuration.setAttribute(ConfigurationConstants.isCleanAttribute, m_cleanButton.getSelection());
					showApplyButton();
				}
			}
		});
		
		// 5 row
		m_reloadButton = new Button(composite, SWT.CHECK);
		m_reloadButton.setText("Reload application code");
		m_reloadButton.setSelection(false);
		m_reloadButton.setLayoutData(checkBoxAligment);
		m_reloadButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (m_configuration != null)
				{
					m_configuration.setAttribute(ConfigurationConstants.isReloadCodeAttribute, m_reloadButton.getSelection());
					showApplyButton();
				}
			}
		});
		
		// 6 row
		m_traceButton = new Button(composite, SWT.CHECK);
		m_traceButton.setText("Add --trace to command");
		m_traceButton.setSelection(false);
		m_traceButton.setLayoutData(checkBoxAligment);
		m_traceButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (m_configuration != null)
				{
					m_configuration.setAttribute(ConfigurationConstants.isTraceAttribute, m_traceButton.getSelection());
					showApplyButton();
				}
			}
		});
	}
	
	private void encodeEmuNameText(String emuName)
	{
		if (m_configuration != null && m_ymlFile != null)
		{
			try 
			{
				String selProjectPlatform = m_configuration.getAttribute(ConfigurationConstants.platforrmCfgAttribute, "");
				
				if (selProjectPlatform.equals(PlatformType.platformAdroid))
				{
					m_configuration.setAttribute(ConfigurationConstants.androidEmuNameAttribute, emuName);
					
					if (!emuName.equals(""))
					{
						m_ymlFile.setAndroidEmuName(emuName);
					}
					else 
					{				
						if (emuName == null || !emuName.equals(""))
						{
							m_ymlFile.removeAndroidEmuName();
						}
					}
					
					m_ymlFile.save();
					showApplyButton();
				}
				else if (selProjectPlatform.equals(PlatformType.platformBlackBerry))
				{
					String sdkPath = m_ymlFile.getSdkConfigPath();
					String bbVer   = m_ymlFile.getBlackberryVer();
					
					Integer simVer = new Integer(emuName);
					
					SdkYmlFile sdkFile = new SdkYmlFile(sdkPath);
					sdkFile.setBbSimPort(bbVer, simVer);
					sdkFile.save();
					showApplyButton();
				}
			} 
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (CoreException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	protected void encodePlatformTypeCombo(String text)
	{
		m_reloadButton.setVisible(text.equals(RunType.platformRhoSim));
		
		m_configuration.setAttribute(ConfigurationConstants.simulatorType, text);
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

	private void setAndroidEmuName(ILaunchConfigurationWorkingCopy configuration)
	{
		try 
		{
			showAndroidEmuName(false);
			
			String selProjectPlatform = configuration.getAttribute(ConfigurationConstants.platforrmCfgAttribute, "");
			String emuName            = configuration.getAttribute(ConfigurationConstants.androidEmuNameAttribute, "");
			String platformType       = configuration.getAttribute(ConfigurationConstants.simulatorType, "");
			
			if (!platformType.equals(RunType.platformDevice) && selProjectPlatform.equals(PlatformType.platformAdroid))
			{
				showAndroidEmuName(true);
				
				m_androidEmuNameLabel.setText("AVD name");
				m_adroidEmuNameText.setText(emuName);
			}
			else if (selProjectPlatform.equals(PlatformType.platformBlackBerry))
			{
				showAndroidEmuName(true);
				showBbEmuName();
			}
		}
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void showBbEmuName()
	{
		try 
		{
			String sdkPath = m_ymlFile.getSdkConfigPath();
			String bbVer = m_ymlFile.getBlackberryVer();

			SdkYmlFile sdkFile = new SdkYmlFile(sdkPath);
			m_adroidEmuNameText.setText(sdkFile.getBbSimPort(bbVer));
			m_androidEmuNameLabel.setText("Simulator name:");
		} 
		catch (FileNotFoundException e) 
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
			} 
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
				
		configuration.setAttribute(ConfigurationConstants.platforrmCfgAttribute, (String) PlatformType.platformAdroid);		
		configuration.setAttribute(ConfigurationConstants.isCleanAttribute, false);
		configuration.setAttribute(ConfigurationConstants.isReloadCodeAttribute, false);
		configuration.setAttribute(ConfigurationConstants.isTraceAttribute, false);	
		configuration.setAttribute(ConfigurationConstants.simulatorType, RunType.platformRhoSim);
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
			selProjectName        = configuration.getAttribute(ConfigurationConstants.projectNameCfgAttribute, "");
			selProjectPlatform    = configuration.getAttribute(ConfigurationConstants.platforrmCfgAttribute, "");
			boolean isClean       = configuration.getAttribute(ConfigurationConstants.isCleanAttribute, false);
			boolean isRebuild     = configuration.getAttribute(ConfigurationConstants.isReloadCodeAttribute, false);
			boolean isTrace       = configuration.getAttribute(ConfigurationConstants.isTraceAttribute, false);
			selAndroidEmuName     = configuration.getAttribute(ConfigurationConstants.androidEmuNameAttribute, "");
			
			if (selProjectName != "")
			{
				m_selProject = ResourcesPlugin.getWorkspace().getRoot().getProject(selProjectName);
				m_appNameText.setText(selProjectName);
				m_adroidEmuNameText.setText(selAndroidEmuName);

				if (m_selProject.isOpen()) 
				{
					m_ymlFile = AppYmlFile.createFromProject(m_selProject);
				}

				if (selProjectPlatform.equals(PlatformType.platformBlackBerry) && m_ymlFile != null)
				{
					showBbEmuName();
				}
			}
			
			setPlatformCombo(selProjectPlatform);
			setPlatformVersionCombo(configuration.getWorkingCopy());
			setPlatfromTypeCombo(configuration.getWorkingCopy());
			
			m_cleanButton.setSelection(isClean);
			m_reloadButton.setSelection(isRebuild);
			m_traceButton.setSelection(isTrace);
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
	
	private void setPlatfromTypeCombo(ILaunchConfigurationWorkingCopy configuration) throws CoreException
	{
		if (getLaunchConfigurationDialog().getMode().equals(ILaunchManager.DEBUG_MODE))
		{
			m_platformTypeCombo.setEnabled(false);
			m_platformTypeCombo.select(0);
		}
		else
		{
			String platformType = configuration.getAttribute(ConfigurationConstants.simulatorType, "");
			
			m_platformTypeCombo.setEnabled(true);
			
			if (platformType.equals(RunType.platformRhoSim))
			{
				m_platformTypeCombo.select(0);
			}
			else if (platformType.equals(RunType.platformSim))
			{
				m_platformTypeCombo.select(1);
			}
			else if (platformType.equals(RunType.platformDevice))
			{
				m_platformTypeCombo.select(2);
			}
		}
	}
	
	private void setPlatformVersionCombo(ILaunchConfigurationWorkingCopy configuration) 
	{
		try
		{
			int maxAndroidVerIdx = androidVersions.length - 1;
		
			String selProjectPlatform = configuration.getAttribute(ConfigurationConstants.platforrmCfgAttribute, "");
			String selAndroidVer      = configuration.getAttribute(ConfigurationConstants.androidVersionAttribute, androidVersions[maxAndroidVerIdx]);
			String selBlackBarryVer   = configuration.getAttribute(ConfigurationConstants.blackberryVersionAttribute, "");			
			String selIphoneVer       = configuration.getAttribute(ConfigurationConstants.iphoneVersionAttribute, "");
			String selPlatformType    = configuration.getAttribute(ConfigurationConstants.simulatorType, "");

			showVersionCombo(false);
			showAndroidEmuName(false);

			if (selProjectPlatform.equals(PlatformType.platformAdroid))
			{
				if (!selPlatformType.equals(RunType.platformDevice))
				{
					showAndroidVersions();
					showVersionCombo(true);
					showAndroidEmuName(true);
					setAndroidEmuName(configuration);
					
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
			else if (selProjectPlatform.equals(PlatformType.platformBlackBerry))
			{
				List<String> bbVersions = showBbVersions();
				showVersionCombo(true);
				showAndroidEmuName(true);
				
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
			else if (selProjectPlatform.equals(PlatformType.platformIPhone))
			{
				List<String> iphoneVersions = showIphoneVersions();
				showVersionCombo(true);
				
				for (int idx=0; idx < iphoneVersions.size(); idx++)
				{
					String currVer = iphoneVersions.get(idx);
					
					if (currVer.equals(selIphoneVer))
					{
						m_selectPlatformVersionCombo.select(idx);
						break;
					}
				}				
			}
		}
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
	}

	private void setPlatformCombo(String selProjectPlatform)
	{
		int platformIdx = -1;
		
		if (selProjectPlatform.equals(PlatformType.platformAdroid))
		{
			platformIdx = 0;
		}
		else if (selProjectPlatform.equals(PlatformType.platformIPhone))
		{
			platformIdx = 1;
		}
		else if (selProjectPlatform.equals(PlatformType.platformWinMobile))
		{
			platformIdx = 2;
		}
		else if (selProjectPlatform.equals(PlatformType.platformBlackBerry))
		{
			platformIdx = 3;
		}
		else if (selProjectPlatform.equals(PlatformType.platformWp7))
		{
			platformIdx = 4;
		}
		else if (selProjectPlatform.equals(PlatformType.platformSymbian))
		{
			platformIdx = 5;
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
				
				m_configuration.setAttribute(ConfigurationConstants.projectNameCfgAttribute, m_selProject.getName());
				
				try 
				{
					m_ymlFile = AppYmlFile.createFromProject(m_selProject);
					setPlatformVersionCombo(m_configuration);
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
			
			String selPlatform = m_configuration.getAttribute(ConfigurationConstants.platforrmCfgAttribute, "");
			
			PlatformType type = PlatformType.fromString(selPlatform);
			
			if (type == PlatformType.eAndroid)
			{
				m_configuration.setAttribute(ConfigurationConstants.androidVersionAttribute, selVersion);
				m_ymlFile.setAndroidVer(selVersion);
				m_ymlFile.save();
			}
			else if (type == PlatformType.eBb)
			{
				m_configuration.setAttribute(ConfigurationConstants.blackberryVersionAttribute, selVersion);
				m_ymlFile.setBbVer(selVersion);
				m_ymlFile.save();
				
				showBbEmuName();
			}
			else if (type == PlatformType.eIPhone)
			{
				m_configuration.setAttribute(ConfigurationConstants.iphoneVersionAttribute, selVersion);
				m_ymlFile.setIphoneVer(selVersion);
				m_ymlFile.save();
			}
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

	private List<String> showIphoneVersions()
	{
		m_selectPlatformVersionCombo.removeAll();
		
		for (String s: iphoneVersions) 
		{
			m_selectPlatformVersionCombo.add(s);
		}
		
		ArrayList<String> retList = new ArrayList<String>();
		
		for (int i=0; i<iphoneVersions.length; ++i)
		{
			retList.add(iphoneVersions[i]);
		}
		
		return retList;
	}

	private List<String> showBbVersions()
	{
		try 
		{
			m_selectPlatformVersionCombo.removeAll();
			
			String m_ymlSdkPath = m_ymlFile.getSdkConfigPath();
			
			SdkYmlFile sdkYmlFile = new SdkYmlFile(m_ymlSdkPath);
			
			List<String> bbVers = sdkYmlFile.getBbVersions();
			
			for (String s : bbVers) 
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
	
	private void showWrongDeviceMsgBox(String msg)
	{
		MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
		messageBox.setText("Warrning");
		messageBox.setMessage(msg);
		messageBox.open();	
		encodePlatformInformation(platformItems[0]);
		m_selectPlatformCombo.select(0);	
	}
	
	private void encodePlatformInformation(String selPlatform)
	{
		if (selPlatform.equals(platformItems[0]))
		{
			m_configuration.setAttribute(ConfigurationConstants.platforrmCfgAttribute, PlatformType.platformAdroid);
		}
		else if (selPlatform.equals(platformItems[1]))
		{		
			m_configuration.setAttribute(ConfigurationConstants.platforrmCfgAttribute, PlatformType.platformIPhone);
		}
		else if (selPlatform.equals(platformItems[2]))
		{
			m_configuration.setAttribute(ConfigurationConstants.platforrmCfgAttribute, PlatformType.platformWinMobile);
		}	
		else if (selPlatform.equals(platformItems[3]))
		{			
			m_configuration.setAttribute(ConfigurationConstants.platforrmCfgAttribute, PlatformType.platformBlackBerry);
		}
		else if (selPlatform.equals(platformItems[4]))
		{
			m_configuration.setAttribute(ConfigurationConstants.platforrmCfgAttribute, PlatformType.platformWp7);
		}
		else if (selPlatform.equals(platformItems[5]))
		{
			m_configuration.setAttribute(ConfigurationConstants.platforrmCfgAttribute, PlatformType.platformSymbian);
		}
		
		setPlatformVersionCombo(m_configuration);
		setAndroidEmuName(m_configuration);
	}
}
