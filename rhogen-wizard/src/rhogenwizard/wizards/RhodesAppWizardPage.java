package rhogenwizard.wizards;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.FilenameFilter;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.FileSelectionDialog;
import org.eclipse.ui.dialogs.FileSystemElement;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class RhodesAppWizardPage extends WizardPage 
{
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
	
	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) 
	{
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		Label label = new Label(container, SWT.NULL);
		label.setText("&Application folder:");

		m_appFolderText = new Text(container, SWT.BORDER | SWT.SINGLE);
		m_appFolderText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_appFolderText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("&Project name:");

		m_appNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		m_appNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_appNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		initialize();
		dialogChanged();
		setControl(container);
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
		if (getAppFolder().length() == 0) {
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

	public String getAppFolder() {
		return m_appFolderText.getText();
	}

	public String getAppName() {
		return m_appNameText.getText();
	}
}