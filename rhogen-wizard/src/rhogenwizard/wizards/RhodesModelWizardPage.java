package rhogenwizard.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
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

public class RhodesModelWizardPage extends WizardPage 
{
	private Text       m_modelName   = null;
	private Text       m_modelFolder = null;
	private Text       m_modelParams = null;
	private ISelection m_selection   = null;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public RhodesModelWizardPage(ISelection selection) 
	{
		super("wizardPage");
		setTitle("Model information");
		setDescription("Model create description");
		this.m_selection = selection;
	}

	public void createControl(Composite parent) 
	{
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		Label label = new Label(container, SWT.NULL);
		label.setText("&Model name:");

		m_modelName = new Text(container, SWT.BORDER | SWT.SINGLE);
		m_modelName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_modelName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		// for fill 3 columns
		label = new Label(container, SWT.NULL); 
		label.setText("");
		
		label = new Label(container, SWT.NULL);
		label.setText("&Model folder:");

		m_modelFolder = new Text(container, SWT.BORDER | SWT.SINGLE);
		m_modelFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_modelFolder.addModifyListener(new ModifyListener() {
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
		label.setText("&Model parameters:");

		m_modelParams = new Text(container, SWT.BORDER | SWT.SINGLE);
		m_modelParams.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_modelParams.addModifyListener(new ModifyListener() {
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
		m_modelName.setText("Model001");
	}

	/**
	 * Ensures that both text fields are set.
	 */
	private void dialogChanged()
	{		
		if (getModelName().length() == 0) {
			updateStatus("Model name must be specified");
			return;
		}
		
		if (getModelFolder().length() == 0) {
			updateStatus("Model folder must be specified");
			return;
		}
		
		if (getModelParams().length() == 0) {
			updateStatus("Model params must be specified");
			return;
		}

		updateStatus(null);
	}
	
	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */
	private void handleBrowse() 
	{
		DirectoryDialog appDirDialog = new DirectoryDialog(getShell());		
		final String selectAppDir = appDirDialog.open();
		m_modelFolder.setText(selectAppDir);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getModelName() {
		return m_modelName.getText();
	}
	
	public String getModelParams() {
		return m_modelParams.getText();
	}
	
	public String getModelFolder() {
		return m_modelFolder.getText();
	}
}