package rhogenwizard.wizards;

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

public class ZeroPage extends WizardPage
{
	private String m_errorText = null;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public ZeroPage(String errText) 
	{
		super("wizardPage");
		setTitle("Information");
		setDescription("");
		m_errorText = errText;
	}

	public void createControl(Composite parent) 
	{
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		layout.verticalSpacing = 9;
		
		// first row
		Label label = new Label(container, SWT.NULL);
		label.setText(m_errorText);

		initialize();
		setControl(container);
		
		setErrorMessage("");
		setPageComplete(true);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */
	private void initialize() 
	{
	}
}
