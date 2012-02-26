package rhogenwizard.wizards.rhodes;

import java.io.File;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import rhogenwizard.BuildInfoHolder;
import rhogenwizard.project.ProjectFactory;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class AppWizardPage extends WizardPage 
{
	private static final String projNameTemplate = "RhodesApplication";
	private static final int labelWidht = 120;
	private static final int textWidht = 300;
	
	private Table      m_generalAttrsTable = null; 
	private Text       m_appFolderText = null;
	private Text       m_appNameText      = null;
	private String     m_selectAppDir  = null;
	private Button     m_defaultPathButton = null;
	private Button     m_browseButton = null;
	private Button     m_exitsCreateButton = null;
	
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public AppWizardPage(ISelection selection) 
	{
		super("wizardPage");
		setTitle("Rhodes application generator wizard");
		setDescription("Desc wizar");
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
		label.setText("&Project name:");
		
		m_appNameText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		m_appNameText.setLayoutData(textAligment);
		m_appNameText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		label = new Label(composite, SWT.NULL);
		
		// 2 row
		m_defaultPathButton = new Button(composite, SWT.CHECK);
		m_defaultPathButton.setText("Create application in default workspace");
		m_defaultPathButton.setSelection(true);
		m_defaultPathButton.setLayoutData(checkBoxAligment);
		m_defaultPathButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				setControlsForDefaultPath();
				dialogChanged();
			}
		});
		
		// 3 row
		m_exitsCreateButton = new Button(composite, SWT.CHECK);
		m_exitsCreateButton.setText("Create application from existing sources.");
		m_exitsCreateButton.setSelection(false);
		m_exitsCreateButton.setLayoutData(checkBoxAligment);
		m_exitsCreateButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				setControlsForExistingSource();
				dialogChanged();
			}
		});
		
		// 4 row
		label = new Label(composite, SWT.NULL);
		label.setText("&Application folder:");

		m_appFolderText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		m_appFolderText.setLayoutData(textAligment);
		m_appFolderText.setEditable(false);
		m_appFolderText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				dialogChanged();
			}
		});

		m_browseButton = new Button(composite, SWT.PUSH);
		m_browseButton.setText("Browse...");
		m_browseButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				handleBrowse();
			}
		});
	}
	
	protected void setControlsForExistingSource() 
	{
		boolean enableDefPath = m_exitsCreateButton.getSelection();
		
		m_defaultPathButton.setEnabled(!enableDefPath);
		m_browseButton.setEnabled(enableDefPath);
		m_appFolderText.setEnabled(enableDefPath);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) 
	{	
		Composite container = new Composite(parent, SWT.NULL);
		
	    createAppSettingBarControls(container);
	    
        initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */
	private void initialize() 
	{		
		setDescription("");
		
		StringBuilder nameProj = new StringBuilder();
		
		for (int i=1; i<100; ++i)
		{
			nameProj.delete(0, nameProj.length());
			nameProj.append(projNameTemplate);
			nameProj.append(i);
		
			m_appNameText.setText(nameProj.toString());
			
			BuildInfoHolder holder = getBuildInformation();
			
			File projFolder = new File(holder.getProjectLocationFullPath());
			
			if (!projFolder.exists())
				break;
		}
		
		setControlsForDefaultPath();
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */
	private void handleBrowse() 
	{
		DirectoryDialog appDirDialog = new DirectoryDialog(getShell());		
		m_selectAppDir = appDirDialog.open();

		File placeFolder = new File(m_selectAppDir);
		
		if (!placeFolder.isDirectory())
		{
			MessageBox msg = new MessageBox(getShell(), ERROR);
			msg.setMessage("Invalid directory");
			msg.setText("Selected item is not directory.");
			msg.open();
			return;
		}
		
		m_appNameText.setText(placeFolder.getName());
		m_appFolderText.setText(m_selectAppDir);
	}
	
	private void setControlsForDefaultPath()
	{
		boolean enableDefPath = m_defaultPathButton.getSelection();
		
		m_browseButton.setEnabled(!enableDefPath);
		m_appFolderText.setEnabled(!enableDefPath);
	}

	/**
	 * Ensures that both text fields are set.
	 */
	private void dialogChanged()
	{
		String appFolder = getAppFolder();
		boolean isDefultPath = m_defaultPathButton.getSelection();
		
		File appFolderFile = new File(appFolder);
		
		if (ProjectFactory.getInstance().isProjectLocationInWorkspace(appFolder))
			m_appNameText.setEditable(false);
		else
			m_appNameText.setEditable(true);
				
		if (!isDefultPath && (!appFolderFile.isDirectory() || (getAppFolder().length() == 0))) 
		{
			updateStatus("Application folder must be specified");
			return;
		}
	
		if (getAppName().length() == 0) 
		{
			updateStatus("Project name must be specified");
			return;
		}
		
		updateStatus("Press finish for creation of project");
		
		updateStatus(null);
	}

	private void updateStatus(String message)
	{
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	private String getAppFolder() 
	{
		return m_appFolderText.getText();
	}

	private String getAppName() 
	{
		return m_appNameText.getText();
	}
	
	private boolean getExistCreate()
	{
		return m_exitsCreateButton.getSelection();
	}
	
	BuildInfoHolder getBuildInformation()
	{
		BuildInfoHolder newInfo  = new BuildInfoHolder();
		
		newInfo.appDir        = getAppFolder();
		newInfo.appName       = getAppName();
		newInfo.existCreate   = getExistCreate();
		
		if (newInfo.existCreate) {
			newInfo.isInDefaultWs = false;
		}
		else {
			newInfo.isInDefaultWs = m_defaultPathButton.getSelection();
		}
		
		return newInfo;
	}
}