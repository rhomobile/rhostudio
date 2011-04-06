package rhogenwizard.wizards;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.io.File;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import rhogenwizard.BuildInfoHolder;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class RhodesAppWizardPage extends WizardPage 
{
	private static final int labelWidht = 120;
	private static final int textWidht = 300;
	
	private Table      m_generalAttrsTable = null; 
	private Text       m_appFolderText = null;
	private Text       m_appNameText      = null;
	private String     m_selectAppDir  = null;
	private Button     m_defaultPathButton = null;
	private Button     m_browseButton = null;
	
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public RhodesAppWizardPage(ISelection selection) 
	{
		super("wizardPage");
		setTitle("Rhodes application generator wizard");
		setDescription("Desc wizar");
	}
	
	public void createAppSettingBarControls(Composite composite)
	{	
		GridLayout layout = new GridLayout(1, true);
		layout.verticalSpacing = 9;
		
        composite.setLayout(layout);

        RowData labelAligment = new RowData(labelWidht, SWT.DEFAULT);
        RowData textAligment  = new RowData(textWidht, SWT.DEFAULT);
        
		// 1 row
		Composite rowContainer1 = new Composite(composite, SWT.NULL);
		rowContainer1.setLayout(new RowLayout());
		Label label = new Label(rowContainer1, SWT.NULL);
		label.setLayoutData(labelAligment);
		label.setText("&Project name:");
		
		m_appNameText = new Text(rowContainer1, SWT.BORDER | SWT.SINGLE);
		m_appNameText.setLayoutData(textAligment);
		m_appNameText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		// 2 row
		Composite rowContainer2 = new Composite(composite, SWT.NULL);
		rowContainer2.setLayout(new RowLayout());

		m_defaultPathButton = new Button(rowContainer2, SWT.CHECK);
		m_defaultPathButton.setText("Create application in default workspace");
		m_defaultPathButton.setSelection(true);
		m_defaultPathButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				setControlsForDefaultPath();
				dialogChanged();
			}
		});

		// 3 row
		Composite rowContainer3 = new Composite(composite, SWT.NULL);
		rowContainer3.setLayout(new RowLayout());
		
		label = new Label(rowContainer3, SWT.NULL);
		label.setText("&Application folder:");
		label.setLayoutData(labelAligment);

		m_appFolderText = new Text(rowContainer3, SWT.BORDER | SWT.SINGLE);
		m_appFolderText.setLayoutData(textAligment);
		m_appFolderText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				dialogChanged();
			}
		});

		m_browseButton = new Button(rowContainer3, SWT.PUSH);
		m_browseButton.setText("Browse...");
		m_browseButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				handleBrowse();
			}
		});
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
		
		m_appNameText.setText("RhodesApplication1");
		
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
	
	BuildInfoHolder getBuildInformation()
	{
		BuildInfoHolder newInfo  = new BuildInfoHolder();
		
		newInfo.appDir = getAppFolder();
		newInfo.appName = getAppName();
		
		newInfo.isInDefaultWs = m_defaultPathButton.getSelection();
		
		return newInfo;
	}
}