package rhogenwizard.wizards;

import java.io.File;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
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
	private static final int      marginSize = 8;

	private Table      m_generalAttrsTable = null; 
	private Text       m_appFolderText = null;
	private Text       m_appNameText      = null;
	private ISelection m_selection     = null;
	private String     m_selectAppDir  = null;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public RhodesAppWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("Rhodes application generator wizard");
		setDescription("Desc wizar");
		this.m_selection = selection;
	}
	
	public void createAppSettingBarControls(Composite composite)
	{
	   GridLayout layout = new GridLayout ();
		  
	   // create controls for first expand 
       layout.marginLeft = layout.marginTop=layout.marginRight=layout.marginBottom=8;
       layout.verticalSpacing = 10;
       composite.setLayout(layout);

		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		Label label = new Label(composite, SWT.NULL);
		label.setText("&Application folder:");

		m_appFolderText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		m_appFolderText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_appFolderText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button button = new Button(composite, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		
		label = new Label(composite, SWT.NULL);
		label.setText("&Project name:");

		m_appNameText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		m_appNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_appNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
	}
	
	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) 
	{	
	    ExpandBar bar = new ExpandBar (parent, SWT.V_SCROLL);
	    Composite composite = new Composite (bar, SWT.NONE);

	    createAppSettingBarControls(composite);
	   
	    // create first expand bar
	    ExpandItem itemCommonSetting = new ExpandItem (bar, SWT.NONE, 0);
	    itemCommonSetting.setText("Common application attributes");
	    itemCommonSetting.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	    itemCommonSetting.setControl(composite);
	    itemCommonSetting.setExpanded(true);
	    
	    // -------------------------------------- //
		  
	    composite = new Composite (bar, SWT.NONE);
	    GridLayout layout = new GridLayout (2, false);
	    layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 8;
	    layout.verticalSpacing = 10;
	    composite.setLayout(layout);  
	    
	    m_generalAttrsTable = new Table(composite, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL);

	    String[] attributesText = BuildInfoHolder.getAttributesStrings();
	    
	    for(int i=0; i<attributesText.length; ++i)
	    {
		    TableItem item = new TableItem(m_generalAttrsTable, SWT.NONE);
		    item.setText(attributesText[i]);
	    }
	    
	    ExpandItem itemGeneralAttr = new ExpandItem (bar, SWT.NONE, 1);
	    itemGeneralAttr.setText("General attributes");
	    itemGeneralAttr.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	    itemGeneralAttr.setControl(composite);
	    itemGeneralAttr.setExpanded(false);
	  
        initialize();
		dialogChanged();
		setControl(composite);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */
	private void initialize() 
	{	
		m_appNameText.setText("RhodesApplication1");
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

	/**
	 * Ensures that both text fields are set.
	 */
	private void dialogChanged()
	{
		String appFolder = getAppFolder();
		
		File appFolderFile = new File(appFolder);
				
		if (!appFolderFile.isDirectory() || (getAppFolder().length() == 0)) {
			updateStatus("Application folder must be specified");
			return;
		}
	
		if (getAppName().length() == 0) {
			updateStatus("Project name must be specified");
			return;
		}
		
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	private String getAppFolder() {
		return m_appFolderText.getText();
	}

	private String getAppName() {
		return m_appNameText.getText();
	}
	
	BuildInfoHolder getBuildInformation()
	{
		BuildInfoHolder newInfo  = new BuildInfoHolder();
		
		newInfo.m_appDir = getAppFolder();
		newInfo.m_appName = getAppName();
		
		newInfo.isPretend = m_generalAttrsTable.getItem(0).getChecked();
		newInfo.isForce   = m_generalAttrsTable.getItem(1).getChecked();
		newInfo.isSkip    = m_generalAttrsTable.getItem(2).getChecked();
		newInfo.isDelete  = m_generalAttrsTable.getItem(3).getChecked();
		newInfo.isDebug   = m_generalAttrsTable.getItem(4).getChecked();
		
		return newInfo;
	}
}