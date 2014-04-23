package rhogenwizard.builder.rhodes;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import rhogenwizard.PlatformType;

public class SelectPlatformDialog extends TitleAreaDialog 
{
	private PlatformType m_selectPlaform = PlatformType.eUnknown;
		
	private Combo  m_platfromCombo = null;

	public SelectPlatformDialog(Shell parentShell) 
	{
		super(parentShell);
	}

	@Override
	public void create() 
	{
		super.create();
		setTitle("Production build dialog");
		setMessage("Producion build for selected platform.", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);

		createFirstName(container);

		return area;
	}

	private void createFirstName(Composite container) 
	{
		Label tokenLabel = new Label(container, SWT.NONE);
		tokenLabel.setText("Platform: ");

		GridData dataFirstName = new GridData();
		dataFirstName.grabExcessHorizontalSpace = true;
		dataFirstName.horizontalAlignment = GridData.FILL;

		m_platfromCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SIMPLE);
		m_platfromCombo.setItems(PlatformType.getPublicIds());
		m_platfromCombo.select(0);
		m_platfromCombo.setLayoutData(dataFirstName);
	}

	@Override
	protected boolean isResizable() 
	{
		return true;
	}

	private void saveInput() 
	{
		m_selectPlaform = PlatformType.fromPublicId(m_platfromCombo.getText());
	}

	@Override
	protected void okPressed() 
	{
		saveInput();
		super.okPressed();
	}

	public PlatformType getSelectedPlatform()
	{
		return m_selectPlaform;
	}
}