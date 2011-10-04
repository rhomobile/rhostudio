package rhogenwizard.wizards;

import java.io.File;

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

import rhogenwizard.BuildInfoHolder;

public class RhodesSourceAdapterWizardPage extends WizardPage 
{
	private Text       m_modelName   = null;
	private Text       m_modelParams = null;
	private ISelection m_selection   = null;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public RhodesSourceAdapterWizardPage(ISelection selection) 
	{
		super("wizardPage");
		setTitle("Source adapter information");
		setDescription("Source adapter create");
		this.m_selection = selection;
	}

	public void createControl(Composite parent) 
	{
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		// first row
		Label label = new Label(container, SWT.NULL);
		label.setText("&Source Adapter name:");

		m_modelName = new Text(container, SWT.BORDER | SWT.SINGLE);
		m_modelName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_modelName.addModifyListener(new ModifyListener() {
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
		m_modelName.setText("Adapter001");
	}

	/**
	 * Ensures that both text fields are set.
	 */
	private void dialogChanged()
	{		
		if (getAdapterName().length() == 0) {
			updateStatus("Adapter name must be specified");
			return;
		}

		updateStatus(null);
	}

	private void updateStatus(String message) 
	{
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getAdapterName() {
		return m_modelName.getText();
	}
	
	public String getModelParams() {
		return m_modelParams.getText();
	}
}