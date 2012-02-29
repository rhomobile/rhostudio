package rhogenwizard.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ZeroPage extends WizardPage
{
	private final static int zeroPageWidth  = 500;
	private final static int zeroPageHeight = 250;
	
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

		setControl(container);

		getShell().setSize(ZeroPage.zeroPageWidth, ZeroPage.zeroPageHeight);
		
		setErrorMessage("");
		setPageComplete(true);
	}
}
