package rhogenwizard.launcher.rhodes;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
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

import rhogenwizard.DialogUtils;
import rhogenwizard.constants.ConfigurationConstants;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;
import rhogenwizard.rhohub.RemotePlatformDesc;
import rhogenwizard.rhohub.RemotePlatformList;
import rhogenwizard.rhohub.RhoHub;
/*
public class RhohubParameterTab extends JavaLaunchTab 
{
    class CustomSelectionListener extends SelectionAdapter
    {
        public void widgetSelected(SelectionEvent e)
        {
            if (m_configuration != null)
            {
                enableControls();
                pageChanged();
            }
        }
    }
    
    class CustomModifyListener implements ModifyListener
    {
        public void modifyText(ModifyEvent e) 
        {
            if (m_configuration != null)
            {
                pageChanged();
            }
        }
    }
    
    private static int    minTabSize      = 650;
 
    private static String[] platformItems = {};
    
    Composite      m_comp = null;
    
    private Text   m_userTokenText     = null;
    private Text   m_rhohubServerText  = null;    
    private Combo  m_selectPlatformCombo = null;
    private Button m_useRhoHubButton = null;
    private Button m_useOnlyRhoHubButton = null;
    
    protected IProject  m_selProject  = null;
    
    protected ILaunchConfigurationWorkingCopy m_configuration = null;
        
    @SuppressWarnings("restriction")
    @Override
    public void createControl(final Composite parent)
    {
        Composite composite = SWTFactory.createComposite(parent, 1, 1, GridData.FILL_HORIZONTAL);
        m_comp = composite;
        
        Composite namecomp = SWTFactory.createComposite(composite, composite.getFont(), 3, 1, GridData.FILL_HORIZONTAL, 0, 0);

        GridData checkBoxAligment = new GridData();
        checkBoxAligment.horizontalAlignment = GridData.FILL;
        checkBoxAligment.horizontalSpan = 3;
        
        // 1 row
        m_useRhoHubButton = new Button(namecomp, SWT.CHECK);
        m_useRhoHubButton.setText("Build application on RhoHub");
        m_useRhoHubButton.setSelection(false);
        m_useRhoHubButton.setLayoutData(checkBoxAligment);
        m_useRhoHubButton.addSelectionListener(new CustomSelectionListener()); 

        // 2 row
        m_useOnlyRhoHubButton = new Button(namecomp, SWT.CHECK);
        m_useOnlyRhoHubButton.setText("Build only remote");
        m_useOnlyRhoHubButton.setSelection(false);
        m_useOnlyRhoHubButton.setLayoutData(checkBoxAligment);
        m_useOnlyRhoHubButton.addSelectionListener(new CustomSelectionListener()); 
        
        // 3 row
        Label label = SWTFactory.createLabel(namecomp, "&User token:", SWT.NULL);
        
        m_userTokenText = SWTFactory.createText(namecomp, SWT.BORDER | SWT.SINGLE, 1);
        m_userTokenText.addModifyListener(new CustomModifyListener()); 
        
        label = new Label(namecomp, SWT.NULL);
        
        // 4 row
        label = SWTFactory.createLabel(namecomp, "&Server url:", SWT.NULL);

        m_rhohubServerText = SWTFactory.createText(namecomp, SWT.BORDER | SWT.SINGLE, 1);
        m_rhohubServerText.addModifyListener(new CustomModifyListener()); 
        
        label = new Label(namecomp, SWT.NULL);
        
        // 2 row
        SWTFactory.createLabel(namecomp, "Platform:", 1); 
        
        m_selectPlatformCombo = SWTFactory.createCombo(namecomp, SWT.READ_ONLY, 1, platformItems);
        m_selectPlatformCombo.addSelectionListener(new CustomSelectionListener());
        m_selectPlatformCombo.select(0);    
        
        label = new Label(namecomp, SWT.NULL);
                    
        enableControls();
    }
    
    private void enableControls()
    {
        m_userTokenText.setEnabled(m_useRhoHubButton.getSelection());
        m_rhohubServerText.setEnabled(m_useRhoHubButton.getSelection());    
        m_selectPlatformCombo.setEnabled(m_useRhoHubButton.getSelection());
        m_useOnlyRhoHubButton.setEnabled(m_useRhoHubButton.getSelection());
    }
    
    private void disableControls()
    {
        m_userTokenText.setEnabled(false);
        m_rhohubServerText.setEnabled(false);    
        m_selectPlatformCombo.setEnabled(false);
        m_useOnlyRhoHubButton.setEnabled(false);
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
                
        m_configuration.setAttribute(ConfigurationConstants.rhoHubUrl, "");
        m_configuration.setAttribute(ConfigurationConstants.rhoHubToken, "");
        m_configuration.setAttribute(ConfigurationConstants.isUseRhoHub, false);
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) 
    {
        Control scrollParent = getLaunchConfigurationDialog().getActiveTab().getControl().getParent();
        
        if (scrollParent instanceof ScrolledComposite)
        {
            ScrolledComposite sc = (ScrolledComposite)scrollParent;
            sc.setMinSize(scrollParent.computeSize(minTabSize, SWT.DEFAULT));   
        }
        
        try
        {
            m_rhohubServerText.setText(configuration.getAttribute(ConfigurationConstants.rhoHubUrl, ""));
            m_userTokenText.setText(configuration.getAttribute(ConfigurationConstants.rhoHubToken, ""));
            m_useRhoHubButton.setSelection(configuration.getAttribute(ConfigurationConstants.isUseRhoHub, false));
            
            RemotePlatformList remotePlatforms = null; //RhoHub.getInstance(configuration).getPlatformList();
            
            if (remotePlatforms == null)
            {
                DialogUtils.error("Connect error", "Rhohub server is not avaialible");
                disableControls();
                return;
            }
            else
            {
                for (RemotePlatformDesc d : remotePlatforms)
                {
                    m_selectPlatformCombo.add(d.getPublicName());    
                }
            }
            
            enableControls();
            pageChanged();
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }            
    }
    
    private void encodePlatformInformation(String selPlatformName)
    {
        
    }
    
    private void pageChanged()
    {
        if (m_configuration == null)
            return;
        
        m_configuration.setAttribute(ConfigurationConstants.rhoHubUrl, m_rhohubServerText.getText());
        m_configuration.setAttribute(ConfigurationConstants.rhoHubToken, m_userTokenText.getText());
        m_configuration.setAttribute(ConfigurationConstants.isUseRhoHub, m_useRhoHubButton.getSelection());
        
        showApplyButton();
    }
    
    @Override
    public boolean canSave()
    {
        return true;
    }

    @Override
    public String getName() 
    {
        return "RhoHub setting";
    }
    
    protected void showApplyButton()
    {
        this.setDirty(false);
        this.getLaunchConfigurationDialog().updateButtons();        
    }    
}
*/