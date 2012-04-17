package rhogenwizard.wizards.rhodes;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ExtensionWizardPage extends WizardPage 
{
	private Text       m_extName   = null;
	private ISelection m_selection = null;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public ExtensionWizardPage(ISelection selection) 
	{
		super("wizardPage");
		setTitle("Native extension information");
		setDescription("Native extension create");
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
		label.setText("&Extension name:");

		m_extName = new Text(container, SWT.BORDER | SWT.SINGLE);
		m_extName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_extName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		// for fill 3 columns
		label = new Label(container, SWT.NULL); 
		label.setText("");
		
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */
	private void initialize() 
	{
		m_extName.setText("Extension");
	}

	/**
	 * Ensures that both text fields are set.
	 */
	private void dialogChanged()
	{		
		if (getExtName().length() == 0) 
		{
			updateStatus("Extension name must be specified");
			return;
		}

		updateStatus(null);
	}

	private void updateStatus(String message)
	{
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getExtName() 
	{
		return m_extName.getText();
	}	
}