package rhogenwizard.wizards.rhohub;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import rhogenwizard.Activator;
import rhogenwizard.constants.ConfigurationConstants;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class BuildCredentialPage extends WizardPage 
{
	private Text m_hubToken = null;
	private Text m_hubUrl   = null;
	
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public BuildCredentialPage() 
	{
		super("wizardPage");
		setTitle("RhoHub build application wizard");
		setDescription("Setup your credential information for RhoHub");
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
		//dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */
	private void initialize() 
	{		
		setDescription("");
		
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        if (store == null)
            return;
       
		m_hubToken.setText(store.getString(ConfigurationConstants.rhoHubToken));		
		m_hubUrl.setText(store.getString(ConfigurationConstants.rhoHubUrl)); 
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

	    IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        if (store == null)
            return;
        
        store.setValue(ConfigurationConstants.rhoHubUrl, m_hubUrl.getText());
        store.setValue(ConfigurationConstants.rhoHubToken, m_hubToken.getText());
        
        updateStatus("Press finish for creation of project");
		updateStatus(null);
	}

	private void updateStatus(String message)
	{
		setErrorMessage(message);
		setPageComplete(message == null);
	}
}