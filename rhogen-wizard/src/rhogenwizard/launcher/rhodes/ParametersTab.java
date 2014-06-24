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
import rhogenwizard.BuildType;
import rhogenwizard.RunType;
import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.constants.ConfigurationConstants;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;

@SuppressWarnings("restriction")
public class ParametersTab extends  JavaLaunchTab 
{
	private static final String iphoneDeviceMsg = "For iphone platform we can't deploy application on device, use iTunes for deploy the application on device.";

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

	Composite 	m_comp = null;
	Combo 	  	m_selectPlatformCombo = null;
	Combo       m_selectPlatformVersionCombo = null;
	Combo       m_selectBuildCombo = null;
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
        SWTFactory.createLabel(namecomp, "Build:", 1); 
        
        m_selectBuildCombo = SWTFactory.createCombo(namecomp, SWT.READ_ONLY, 1, BuildType.getPublicIds());
        m_selectBuildCombo.addSelectionListener(new SelectionAdapter()
        {   
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
                if (m_configuration != null)
                {           
                    if (m_selectBuildCombo.getText().equals(BuildType.eRhoMobileCom.publicId) && 
                        m_platformTypeCombo.getText().equals(RunType.eRhoSimulator.publicId))
                    {
                        selectByItem(m_platformTypeCombo, RunType.eDevice.publicId);
                    }

                    encodeBuildInformation(m_selectBuildCombo.getText());
                    showApplyButton();
                }
            }
        });
        m_selectBuildCombo.select(0);        
            
        SWTFactory.createLabel(namecomp, "", 1);
        
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
					if ((m_platformTypeCombo.getText().equals(RunType.eDevice.publicId) ||
					    m_selectBuildCombo.getText().equals(BuildType.eRhoMobileCom.publicId)) &&
					    m_selectPlatformCombo.getText().equals(PlatformType.eIPhone.publicId))
					{
					    DialogUtils.warning("Warning", iphoneDeviceMsg);
					    selectByItem(m_selectBuildCombo, BuildType.eLocal.publicId); 
					    selectByItem(m_platformTypeCombo, RunType.eRhoSimulator.publicId);
					    return;
					}

                    encodePlatformInformation(m_selectPlatformCombo.getText());
                    setPlatfromTypeCombo(m_configuration);
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
		
		m_platformTypeCombo = SWTFactory.createCombo(namecomp, SWT.READ_ONLY, 1, RunType.getPublicIds());
		m_platformTypeCombo.addSelectionListener(new SelectionAdapter()
		{	
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if (m_configuration != null)
				{
				    // for iphone platform we can't deploy application on device, it's need to do by hand
				    if (m_platformTypeCombo.getText().equals(RunType.eDevice.publicId) &&
				        m_selectPlatformCombo.getText().equals(PlatformType.eIPhone.publicId))
				    {
				        DialogUtils.warning("Warning", iphoneDeviceMsg);
				        selectByItem(m_platformTypeCombo, RunType.eRhoSimulator.publicId);
				    }
				    
				    // for win32
				    if (m_platformTypeCombo.getText().equals(RunType.eDevice.publicId) &&
				        m_selectPlatformCombo.getText().equals(PlatformType.eWin32.publicId))
				    {
				        DialogUtils.warning("Warning", "For Win32 platform we can run only simulator build.");
				        selectByItem(m_platformTypeCombo, RunType.eEmulator.publicId);
				    }
                    
					encodePlatformTypeCombo(RunType.fromPublicId(m_platformTypeCombo.getText()));
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
				String selProjectPlatform = m_configuration.getAttribute(ConfigurationConstants.platformCfgAttribute, "");
				
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
			} 
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
			catch (CoreException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	protected void encodePlatformTypeCombo(RunType runType)
	{
		m_reloadButton.setVisible(runType == RunType.eRhoSimulator);
		
		m_configuration.setAttribute(ConfigurationConstants.simulatorType, runType.id);
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
			
			String selProjectPlatform = configuration.getAttribute(ConfigurationConstants.platformCfgAttribute, "");
			String emuName            = configuration.getAttribute(ConfigurationConstants.androidEmuNameAttribute, "");
			String runTypeId          = configuration.getAttribute(ConfigurationConstants.simulatorType, "");
			
			if (!runTypeId.equals(RunType.eDevice.id) && selProjectPlatform.equals(PlatformType.eAndroid.id))
			{
				showAndroidEmuName(true);
				
				m_androidEmuNameLabel.setText("AVD name");
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
			} 
			catch (FileNotFoundException e)
			{
				MessageDialog.openError(getShell(), "Error", "File build.yml not exists or corrupted. Project - " + getSelectProject().getName());
				e.printStackTrace();
			}
		}
				
        configuration.setAttribute(ConfigurationConstants.buildCfgAttribute, BuildType.eRhoMobileCom.id);
		configuration.setAttribute(ConfigurationConstants.platformCfgAttribute, PlatformType.eAndroid.id);
		configuration.setAttribute(ConfigurationConstants.isCleanAttribute, false);
		configuration.setAttribute(ConfigurationConstants.isReloadCodeAttribute, false);
		configuration.setAttribute(ConfigurationConstants.isTraceAttribute, false);	
		configuration.setAttribute(ConfigurationConstants.simulatorType, RunType.eRhoSimulator.id);
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
			
			String selProjectName = null, selProjectPlatform = null, selProjectBuild = null, selAndroidEmuName = null;
			selProjectName        = configuration.getAttribute(ConfigurationConstants.projectNameCfgAttribute, "");
			selProjectPlatform    = configuration.getAttribute(ConfigurationConstants.platformCfgAttribute, "");
			selProjectBuild       = configuration.getAttribute(ConfigurationConstants.buildCfgAttribute, "");
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
			}
			
            setPlatformCombo(selProjectPlatform);
            setBuildCombo(selProjectBuild);
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
	
	private void setPlatfromTypeCombo(ILaunchConfigurationWorkingCopy configuration)
	{
		PlatformType selProjectPlatform = PlatformType.fromId(getStringAttr(
		    configuration, ConfigurationConstants.platformCfgAttribute, null
		));
		
		boolean debugMode = getLaunchConfigurationDialog().getMode().equals(ILaunchManager.DEBUG_MODE);
		boolean android = selProjectPlatform == PlatformType.eAndroid;
		boolean iphone = selProjectPlatform == PlatformType.eIPhone;
		if (debugMode && !android && !iphone)
		{
			m_platformTypeCombo.setEnabled(false);
			selectByItem(m_platformTypeCombo, RunType.eRhoSimulator.publicId);
		}
		else
		{
			RunType runType = RunType.fromId(getStringAttr(
			    configuration, ConfigurationConstants.simulatorType, null
			));
			
			m_platformTypeCombo.setEnabled(true);
			selectByItem(m_platformTypeCombo, runType.publicId);
		}
	}

    private void setBuildCombo(ILaunchConfigurationWorkingCopy configuration)
    {
        BuildType selProjectBuild;
        try
        {
            selProjectBuild = BuildType.fromId(configuration.getAttribute(ConfigurationConstants.buildCfgAttribute, ""));
        }
        catch (CoreException e)
        {
            e.printStackTrace();
            return;
        }
        selectByItem(m_selectBuildCombo, selProjectBuild.publicId);
    }
	
	protected void setPlatformVersionCombo(ILaunchConfigurationWorkingCopy configuration) 
	{
		try
		{
			int maxAndroidVerIdx = androidVersions.length - 1;
		
			String selProjectPlatform = configuration.getAttribute(ConfigurationConstants.platformCfgAttribute, "");
			String selAndroidVer      = configuration.getAttribute(ConfigurationConstants.androidVersionAttribute, androidVersions[maxAndroidVerIdx]);
			String selIphoneVer       = configuration.getAttribute(ConfigurationConstants.iphoneVersionAttribute, "");
			String runTypeId          = configuration.getAttribute(ConfigurationConstants.simulatorType, "");

			showVersionCombo(false);
			showAndroidEmuName(false);

			if (selProjectPlatform.equals(PlatformType.eAndroid.id))
			{
				if (!runTypeId.equals(RunType.eDevice.id))
				{
			        m_selectPlatformVersionCombo.setItems(androidVersions); 

					showVersionCombo(true);
					showAndroidEmuName(true);
					setAndroidEmuName(configuration);
					
                    selectByItem(m_selectPlatformVersionCombo, selAndroidVer);
				}
			}
			else if (selProjectPlatform.equals(PlatformType.eIPhone.id))
			{
	            m_selectPlatformVersionCombo.setItems(iphoneVersions);
				
	            showVersionCombo(true);
                
	            selectByItem(m_selectPlatformVersionCombo, selIphoneVer);
			}
		}
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
	}

	private void setPlatformCombo(String selProjectPlatform)
	{
	    selectByItem(m_selectPlatformCombo, PlatformType.fromId(selProjectPlatform).publicId);
	}
		
    private void setBuildCombo(String selBuildPlatform)
    {
        selectByItem(m_selectBuildCombo, BuildType.fromId(selBuildPlatform).publicId);
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
			String selPlatform = m_configuration.getAttribute(ConfigurationConstants.platformCfgAttribute, "");
			
			PlatformType type = PlatformType.fromId(selPlatform);
			
			if (type == PlatformType.eAndroid)
			{
				m_configuration.setAttribute(ConfigurationConstants.androidVersionAttribute, selVersion);
				m_ymlFile.setAndroidVer(selVersion);
				m_ymlFile.save();
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
	}
	
	private void encodePlatformInformation(String selPlatform)
	{
		PlatformType pt = PlatformType.fromPublicId(selPlatform);
		if (pt != PlatformType.eUnknown)
		{
			m_configuration.setAttribute(ConfigurationConstants.platformCfgAttribute, pt.id);
		}
		
		setPlatformVersionCombo(m_configuration);
		setAndroidEmuName(m_configuration);
	}

    private void encodeBuildInformation(String selBuild)
    {
        BuildType bt = BuildType.fromPublicId(selBuild);
        if (bt != BuildType.eUnknown)
        {
            m_configuration.setAttribute(ConfigurationConstants.buildCfgAttribute, bt.id);
        }
        
        setBuildCombo(m_configuration);
    }

	protected IProject getSelectProject()
	{
		return m_selProject;
	}

    private static void selectByItem(Combo combo, String item)
    {
        if (item != null)
        {
            combo.select(combo.indexOf(item));
        }
    }

    private static String getStringAttr(
        ILaunchConfiguration configuration, String attributeName, String defaultValue)
    {
        try
        {
            return configuration.getAttribute(attributeName, defaultValue);
        }
        catch (CoreException e)
        {
            return defaultValue;
        }
    }
}
