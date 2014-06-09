package rhogenwizard.builder.rhodes;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import rhogenwizard.BuildType;
import rhogenwizard.PlatformType;

public class ConfigProductionBuildDialog extends TitleAreaDialog 
{
    private PlatformType platformType = PlatformType.eUnknown;
    private BuildType buildType = BuildType.eUnknown;
		
    private Combo platfromCombo = null;
    private Combo buildCombo = null;

	public ConfigProductionBuildDialog(Shell parentShell) 
	{
		super(parentShell);
	}

	@Override
	public void create() 
	{
		super.create();
		setTitle("Production build");
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(new GridLayout(2, false));

        buildCombo    = makeCombo(container, "Build"   , 0, BuildType   .getPublicIds());
        platfromCombo = makeCombo(container, "Platform", 0, PlatformType.getPublicIds());

		return area;
	}
	
	private Combo makeCombo(Composite container, String label, int def, String[] values) {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;

        new Label(container, SWT.NONE).setText(label + ": ");
        Combo combo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SIMPLE);
        combo.setItems(values);
        combo.select(def);
        combo.setLayoutData(gridData);
        
        return combo;
	}

	@Override
	protected boolean isResizable() 
	{
		return true;
	}

	@Override
	protected void okPressed() 
	{
        platformType = PlatformType.fromPublicId(platfromCombo.getText());
        buildType    = BuildType   .fromPublicId(buildCombo   .getText());
		super.okPressed();
	}

    public PlatformType platformType()
    {
        return platformType;
    }

    public BuildType buildType()
    {
        return buildType;
    }
}