package rhogenwizard.wizards.rhohub;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.rhohub.IRhoHubSettingSetter;
import rhogenwizard.rhohub.RhoHubBundleSetting;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class BuildCredentialPage extends WizardPage 
{
    private IProject m_project = null;
    
	private Text m_hubToken = null;
	private Text m_hubUrl   = null;
	
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public BuildCredentialPage(IProject project) 
	{
		super("wizardPage");
		setTitle("RhoHub build application wizard");
		setDescription("Setup your credential information for RhoHub");
		
		m_project = project;
	}
	
	public void createAppSettingBarControls(Composite composite)
	{	
		GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = 9;
		
        composite.setLayout(layout);
        
        GridData textAligment = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
        
        GridData checkBoxAligment = new GridData();
        checkBoxAligment.horizontalAlignment = GridData.FILL;
        checkBoxAligment.horizontalSpan = 3;
        			
		// 1 row
		Label label = new Label(composite, SWT.NULL);
		label.setText("&User token:");
		
		m_hubToken = new Text(composite, SWT.BORDER | SWT.SINGLE);
		m_hubToken.setLayoutData(textAligment);
		m_hubToken.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				dialogChanged();
			}
		});
		
		label = new Label(composite, SWT.NULL);
		
        // 2 row
        label = new Label(composite, SWT.NULL);
        label.setText("&Server url:");
        
        m_hubUrl = new Text(composite, SWT.BORDER | SWT.SINGLE);
        m_hubUrl.setLayoutData(textAligment);
        m_hubUrl.addModifyListener(new ModifyListener() 
        {
            public void modifyText(ModifyEvent e) 
            {
                dialogChanged();
            }
        });
        
        label = new Label(composite, SWT.NULL);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) 
	{	
		Composite container = new Composite(parent, SWT.NULL);
		
	    createAppSettingBarControls(container);
	    
        initialize();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */
	private void initialize() 
	{		
		setDescription("");
		
		IRhoHubSetting store = RhoHubBundleSetting.createGetter(m_project);
		
        if (store == null)
            return;
       
		m_hubToken.setText(store.getToken());		
		m_hubUrl.setText(store.getServerUrl()); 
	}

	/**
	 * Ensures that both text fields are set.
	 */
	private void dialogChanged()
	{
		if (m_hubUrl.getText().isEmpty())
		{
		    updateStatus("RhoHub server url should be assigned");
		    return;
		}

	    if (m_hubToken.getText().isEmpty())
	    {
	        updateStatus("User token should be assigned");
	        return;
	    }

        try
        {
    	    IRhoHubSettingSetter store = RhoHubBundleSetting.createSetter(m_project);
    
            if (store == null)
                return;
        
            store.setServerUrl(m_hubUrl.getText());
            store.setToken(m_hubToken.getText());
        }
        catch (BackingStoreException e)
        {
            updateStatus("Unhandled exception, close the wizard and try again");
            e.printStackTrace();
        }
        
        updateStatus("Press finish for creation of project");
		updateStatus(null);
	}

	private void updateStatus(String message)
	{
		setErrorMessage(message);
		setPageComplete(message == null);
	}
}