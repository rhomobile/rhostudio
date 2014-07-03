package rhogenwizard.builder.rhodes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import rhogenwizard.Activator;
import rhogenwizard.BuildType;
import rhogenwizard.PlatformType;
import rhogenwizard.RhodesStore;
import rhogenwizard.UiUtils;
import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;

public class ConfigProductionBuildDialog extends TitleAreaDialog 
{
    private IProject project = null;
    private PlatformType platformType = PlatformType.eUnknown;
    private BuildType buildType = BuildType.eUnknown;
		
    private Combo projectCombo = null;
    private Combo platformCombo = null;
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

        projectCombo = makeCombo(container, "Project", getProjectPublicIds());
        platformCombo = makeCombo(container, "Platform", PlatformType.getPublicIds());
        platformCombo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateUi();
            }
        });
        buildCombo = makeCombo(container, "Build", new String[0]);


        RhodesStore store = new RhodesStore(Activator.getDefault().getPreferenceStore());

        UiUtils.selectByItem(projectCombo, store.productionBuildProjectName());
        UiUtils.selectByItem(platformCombo, store.productionBuildPlatform().publicId);
        updateUi();
        UiUtils.selectByItem(buildCombo, store.productionBuildBuild().publicId);

        return area;
    }

	@Override
	protected boolean isResizable() 
	{
		return true;
	}

	@Override
	protected void okPressed() 
    {
        project = uiProject();
        platformType = uiPlatformType();
        buildType = uiBuildType();

        RhodesStore store = new RhodesStore(Activator.getDefault().getPreferenceStore());

        store.productionBuildProjectName(project.getName());
        store.productionBuildPlatform(platformType);
        store.productionBuildBuild(buildType);

        super.okPressed();
    }

    public IProject project()
    {
        return project;
    }

    public PlatformType platformType()
    {
        return platformType;
    }

    public BuildType buildType()
    {
        return buildType;
    }

    public static boolean thereAreRhomobileProjects()
    {
        return getProjectPublicIds().length > 0;
    }

    private static File buildYml(IProject project)
    {
        return new File(project.getLocation() + File.separator + AppYmlFile.configFileName);
    }

    private static String[] getProjectPublicIds()
    {
        List<String> list = new ArrayList<String>();

        for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects())
        {
            if (RhodesProject.checkNature(p) || RhoelementsProject.checkNature(p))
            {
                if (p.isOpen() && buildYml(p).exists())
                {
                    list.add(p.getName());
                }
            }
        }

        Collections.sort(list, new Comparator<String>()
        {
            public int compare(String s1, String s2)
            {
                int diff = String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
                return (diff == 0) ? s1.compareTo(s2) : diff;
            }
        });
        return list.toArray(new String[0]);
    }

    private void updateUi()
    {
        UiUtils.updateCombo(buildCombo, getBuildTypePublicIds(uiPlatformType()));
    }

    private Combo makeCombo(Composite container, String label, String[] values) {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;

        new Label(container, SWT.NONE).setText(label + ": ");
        Combo combo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SIMPLE);
        combo.setItems(values);
        combo.select(0);
        combo.setLayoutData(gridData);

        return combo;
    }

    private IProject uiProject()
    {
        return ResourcesPlugin.getWorkspace().getRoot().getProject(projectCombo.getText());
    }

    private PlatformType uiPlatformType()
    {
        return PlatformType.fromPublicId(platformCombo.getText());
    }

    private BuildType uiBuildType()
    {
        return BuildType.fromPublicId(buildCombo.getText());
    }

    private String[] getBuildTypePublicIds(PlatformType pt)
    {
        List<String> list = new ArrayList<String>();

        for (BuildType bt : BuildType.values())
        {
            if (bt.publicId != null)
            {
                boolean include = false;
                switch (bt)
                {
                case eLocal       : include = true                   ; break;
                case eRhoMobileCom: include = pt != PlatformType.eWp7; break;
                default:
                    assert false;
                }

                if (include)
                {
                    list.add(bt.publicId);
                }
            }
        }
        return list.toArray(new String[0]);
    }
}