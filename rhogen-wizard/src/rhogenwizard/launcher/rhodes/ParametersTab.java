package rhogenwizard.launcher.rhodes;

import java.io.FileNotFoundException;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import rhogenwizard.DialogUtils;
import rhogenwizard.PlatformType;
import rhogenwizard.RunType;
import rhogenwizard.WinMobileSdk;
import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.buildfile.SdkYmlFile;
import rhogenwizard.constants.ConfigurationConstants;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;

@SuppressWarnings("restriction")
public class ParametersTab extends  JavaLaunchTab 
{
	private static int    minTabSize      = 650;
		
	protected static String androidVersions[] = { "1.6",
											    "2.1",
											    "2.2",
											    "2.3.1",
											    "2.3.3",
											    "3.0",
											    "3.1",
											    "3.2", 
											    "4.0",
											    "4.0.3" };

	protected static String iphoneVersions[] = { "iphone",
											     "ipad" };

	private static String simulatorTypes[] = { RunType.platformRhoSim,
		                                       RunType.platformSim,
		                                       RunType.platformDevice };
	
    protected static String wmVersions[] = WinMobileSdk.getVersions();


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
	
	protected ILaunchConfigurationWorkingCopy m_configuration = null;
	
	protected AppYmlFile     m_ymlFile = null;
	
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
		
		m_selectPlatformCombo = SWTFactory.createCombo(namecomp, SWT.READ_ONLY, 1, PlatformType.getPublicIds());
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
				    // for iphone platform we can't deploy application on device, it's need to do by hand
				    if (m_platformTypeCombo.getText().equals(RunType.platformDevice) && 
				        m_selectPlatformCombo.getText().equals(PlatformType.eIPhone.publicId))
				    {
				        DialogUtils.warning("Warning", "For iphone platform we can't deploy application on device, use iTunes for deploy the application on device.");
				        m_platformTypeCombo.select(m_platformTypeCombo.indexOf(RunType.platformRhoSim)); // select rhosimuator 
				    }
				    
                    // for win32
                    if (m_platformTypeCombo.getText().equals(RunType.platformDevice) && 
                        m_selectPlatformCombo.getText().equals(PlatformType.eWin32.publicId))
                    {
                        DialogUtils.warning("Warning", "For Win32 platform we can run only simulator build.");
                        m_platformTypeCombo.select(m_platformTypeCombo.indexOf(RunType.platformSim)); // select simulator 
                    }
                    
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
				
				if (selProjectPlatform.equals(PlatformType.eAndroid.id))
				{
					m_configuration.setAttribute(ConfigurationConstants.androidEmuNameAttribute, emuName);
					
					if (!emuName.equals(""))
					{
						m_ymlFile.setAndroidEmuName(emuName);
					}
					else 
					{				
						m_ymlFile.removeAndroidEmuName();
					}
					
					m_ymlFile.save();
					showApplyButton();
				}
				else if (selProjectPlatform.equals(PlatformType.eBb.id))
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
			
			if (!platformType.equals(RunType.platformDevice) && selProjectPlatform.equals(PlatformType.eAndroid.id))
			{
				showAndroidEmuName(true);
				
				m_androidEmuNameLabel.setText("AVD name");
				m_adroidEmuNameText.setText(emuName);
			}
			else if (selProjectPlatform.equals(PlatformType.eBb.id))
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
			
			if (sdkFile.getBbSimPort(bbVer) != null)
				m_adroidEmuNameText.setText(sdkFile.getBbSimPort(bbVer));
			else
				m_adroidEmuNameText.setText("Simulator name is not define in rhobuild.yml file");
			
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
			m_selProject = ProjectFactory.getInstance().getSelectedProject();

			if (m_selProject == null)
			{
				IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				
				for (IProject project : allProjects)
				{
					if (RhodesProject.checkNature(project) || RhoelementsProject.checkNature(project))
					{
						m_selProject = project;	
					}
				}
			}
			else
			{
				if (!RhodesProject.checkNature(m_selProject))
				{
					m_selProject = null;	
				}	
			}
		}
		
		if (m_selProject == null)
		{
			MessageDialog.openInformation(getShell(), "Message", "Create and select rhodes project before create the configuration.");
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
                configuration.setAttribute(ConfigurationConstants.wmVersionAttribute, wmVersions[0]);
			} 
			catch (FileNotFoundException e)
			{
				MessageDialog.openError(getShell(), "Error", "File build.yml not exists or corrupted. Project - " + getSelectProject().getName());
				e.printStackTrace();
			}
		}
				
		configuration.setAttribute(ConfigurationConstants.platforrmCfgAttribute, PlatformType.eAndroid.id);
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

				if (selProjectPlatform.equals(PlatformType.eBb.id) && m_ymlFile != null)
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
			m_platformTypeCombo.select(m_platformTypeCombo.indexOf(RunType.platformRhoSim));
		}
		else
		{
			String platformType = configuration.getAttribute(ConfigurationConstants.simulatorType, "");
			
			m_platformTypeCombo.setEnabled(true);
            m_platformTypeCombo.select(m_platformTypeCombo.indexOf(platformType));
		}
	}
	
	protected void setPlatformVersionCombo(ILaunchConfigurationWorkingCopy configuration) 
	{
		try
		{
			int maxAndroidVerIdx = androidVersions.length - 1;
		
			String selProjectPlatform = configuration.getAttribute(ConfigurationConstants.platforrmCfgAttribute, "");
			String selAndroidVer      = configuration.getAttribute(ConfigurationConstants.androidVersionAttribute, androidVersions[maxAndroidVerIdx]);
			String selBlackBarryVer   = configuration.getAttribute(ConfigurationConstants.blackberryVersionAttribute, "");			
			String selIphoneVer       = configuration.getAttribute(ConfigurationConstants.iphoneVersionAttribute, "");
			String selWmVer           = configuration.getAttribute(ConfigurationConstants.wmVersionAttribute, WinMobileSdk.v6_0.version);
			String selPlatformType    = configuration.getAttribute(ConfigurationConstants.simulatorType, "");

			showVersionCombo(false);
			showAndroidEmuName(false);

			if (selProjectPlatform.equals(PlatformType.eAndroid.id))
			{
				if (!selPlatformType.equals(RunType.platformDevice))
				{
			        m_selectPlatformVersionCombo.setItems(androidVersions); 

					showVersionCombo(true);
					showAndroidEmuName(true);
					setAndroidEmuName(configuration);
					
                    m_selectPlatformVersionCombo.select(m_selectPlatformVersionCombo.indexOf(selAndroidVer));
				}
			}
			else if (selProjectPlatform.equals(PlatformType.eBb.id))
			{
                m_selectPlatformVersionCombo.setItems(getBbVersions()); 

                showVersionCombo(true);
				showAndroidEmuName(true);
				
                m_selectPlatformVersionCombo.select(m_selectPlatformVersionCombo.indexOf(selBlackBarryVer));
			}
			else if (selProjectPlatform.equals(PlatformType.eIPhone.id))
			{
	            m_selectPlatformVersionCombo.setItems(iphoneVersions);
				
	            showVersionCombo(true);
                
	            m_selectPlatformVersionCombo.select(m_selectPlatformVersionCombo.indexOf(selIphoneVer));
			}
            else if (selProjectPlatform.equals(PlatformType.eWm.id))
            {
                m_selectPlatformVersionCombo.setItems(wmVersions);

                showVersionCombo(true);

                m_selectPlatformVersionCombo.select(m_selectPlatformVersionCombo.indexOf(selWmVer));
            }
		}
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
	}

	private void setPlatformCombo(String selProjectPlatform)
	{
	    String publicId = PlatformType.fromId(selProjectPlatform).publicId;
	    m_selectPlatformCombo.select(m_selectPlatformCombo.indexOf(publicId));
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
		
	protected void selectProjectDialog()
	{
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
					getShell(), ResourcesPlugin.getWorkspace().getRoot(), false, "Select project");
			
		if (dialog.open() == ContainerSelectionDialog.OK) 
		{
			Object[] result = dialog.getResult();
			
			if (result.length == 1) 
			{				
				String selProjectName = ((Path) result[0]).toString();
				selProjectName        = selProjectName.replaceAll("/", "");
				IProject selProject   = ResourcesPlugin.getWorkspace().getRoot().getProject(selProjectName);
				
				if (!RhodesProject.checkNature(selProject) && !RhoelementsProject.checkNature(selProject))
				{
					MessageDialog.openError(getShell(), "Message", "Project " + selProject.getName() + " is not rhodes application");
					return;
				}
																
				try 
				{
			        m_ymlFile = AppYmlFile.createFromProject(selProject);
					setPlatformVersionCombo(m_configuration);
				}
				catch (FileNotFoundException e) 
				{
				    MessageDialog.openError(getShell(), "File error", e.toString());
					e.printStackTrace();
					return;
				}
                catch (Exception e) 
                {
                    MessageDialog.openError(getShell(), "Yaml error", e.toString());
                    e.printStackTrace();
                    return;
                }

                // if yaml file not found or corrupt transaction is revert to prev state 
                m_selProject = selProject;
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
	
	protected void showApplyButton()
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
			String selPlatform = m_configuration.getAttribute(ConfigurationConstants.platforrmCfgAttribute, "");
			
			PlatformType type = PlatformType.fromId(selPlatform);
			
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
            else if (type == PlatformType.eWm)
            {
                m_configuration.setAttribute(ConfigurationConstants.wmVersionAttribute, selVersion);
			}
		}
		catch(CoreException e)
		{
			e.printStackTrace();
		}
	}
	
	private String[] getBbVersions()
	{
		try 
		{
			String m_ymlSdkPath = m_ymlFile.getSdkConfigPath();
			
			SdkYmlFile sdkYmlFile = new SdkYmlFile(m_ymlSdkPath);
			
			return sdkYmlFile.getBbVersions().toArray(new String[0]);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		return new String[0];
	}
	
	private void encodePlatformInformation(String selPlatform)
	{
		PlatformType pt = PlatformType.fromPublicId(selPlatform);
		if (pt != PlatformType.eUnknown)
		{
			m_configuration.setAttribute(ConfigurationConstants.platforrmCfgAttribute, pt.id);
		}
		
		setPlatformVersionCombo(m_configuration);
		setAndroidEmuName(m_configuration);
	}
	
	protected IProject getSelectProject()
	{
		return m_selProject;
	}
}
