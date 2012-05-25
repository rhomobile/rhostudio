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

public class ModelWizardPage extends WizardPage 
{
	private Text       m_modelName   = null;
	private Text       m_modelParams = null;
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public ModelWizardPage(ISelection selection) 
	{
		super("wizardPage");
		setTitle("Model information");
		setDescription("Model create");
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
		
		// second row
		label = new Label(container, SWT.NULL);
		label.setText("&Model attributes:");

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
		if (getModelName().length() == 0) 
		{
			updateStatus("Model name must be specified");
			return;
		}
			
		if (!checkModelParams(getModelParams()))
		{
			updateStatus("Model attributes must be a comma-separated list of identifiers.");
			return;
		}

		updateStatus(null);
	}

	private void updateStatus(String message)
	{
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getModelName() 
	{
		return m_modelName.getText();
	}
	
	public String getModelParams() 
	{
		return m_modelParams.getText();
	}

	private boolean checkModelParams(String modelParams)
	{
	    String item = "\\s*\\p{Alpha}\\w*\\s*"; // identifier in spaces
	    return modelParams.matches("^" + item + "(," + item + ")*$");
	}
}
