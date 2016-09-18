package rhogenwizard.launcher.rhodes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import rhogenwizard.PlatformType;
import rhogenwizard.BuildType;
import rhogenwizard.RhodesConfigurationRO;
import rhogenwizard.RhodesConfigurationRW;
import rhogenwizard.RunType;
import rhogenwizard.UiUtils;
import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;

@SuppressWarnings("restriction")
public class ParametersTab extends JavaLaunchTab
{
    private static int minTabSize = 650;

    private static class PlatformVersion
    {
        private static Map<PlatformType, String[]> versions = getVersions();

        private final Combo combo;
        private PlatformType platformType;
        private Map<PlatformType, String> cache;

        public PlatformVersion(Combo combo)
        {
            this.combo = combo;
            this.platformType = PlatformType.eAndroid;
            this.cache = new HashMap<PlatformType, String>();
            for (Map.Entry<PlatformType, String[]> e : versions.entrySet())
            {
                this.cache.put(e.getKey(), e.getValue()[0]);
            }
        }

        public void setEnabled(boolean enabled)
        {
            combo.setEnabled(enabled);
            combo.setVisible(enabled);
        }

        public void switchTo(PlatformType pt)
        {
            if (versions.containsKey(pt))
            {
                cache.put(platformType, combo.getText());
                combo.setItems(versions.get(pt));
                combo.select(0);
                combo.select(combo.indexOf(cache.get(pt)));
                platformType = pt;
            }
        }

        public String getValue(PlatformType pt)
        {
            return (pt == platformType) ? combo.getText() : cache.get(pt);
        }

        public void setValue(PlatformType pt, String value)
        {
            if (pt == platformType)
            {
                combo.select(combo.indexOf(value));
            }
            else
            {
                if (Arrays.asList(versions.get(pt)).contains(value))
                {
                    cache.put(pt, value);
                }
            }
        }

        public static String getLastValue(PlatformType pt)
        {
            String[] list = versions.get(pt);
            return list[list.length - 1];
        }

        private static Map<PlatformType, String[]> getVersions()
        {
            Map<PlatformType, String[]> versions = new HashMap<PlatformType, String[]>();
            versions.put(PlatformType.eAndroid, new String[]{
                "1.6",
                "2.1", "2.2", "2.3.1", "2.3.3",
                "3.0", "3.1", "3.2",
                "4.0", "4.0.3", "4.1.2", "4.2.2", "4.3.1", "4.4.2",
                "5.0.1", "5.1.1",
                "6.0"
            });
            versions.put(PlatformType.eIPhone, new String[]{ "iphone", "ipad" });
            return versions;
        }
    }

    private Composite   m_comp = null;
    private Combo       m_platformTypeCombo = null;
    private Combo       m_runTypeCombo = null;
    private Combo       m_buildTypeCombo = null;
    private Combo       m_platformVersionCombo = null;
    private Text        m_appNameText = null;
    private Text        m_adroidEmuNameText = null;
    private Button      m_cleanButton = null;
    private Label       m_androidEmuNameLabel = null;
    private Button      m_reloadButton = null;
    private Button      m_traceButton = null;

    private IProject    m_selProject  = null;

    private PlatformVersion m_platformVersion = null;

    protected AppYmlFile     m_ymlFile = null;

    @Override
    public void createControl(final Composite parent)
    {
        Composite composite = SWTFactory.createComposite(parent, 1, 1, GridData.FILL_HORIZONTAL);
        m_comp = composite;

        Composite namecomp = SWTFactory.createComposite(composite, composite.getFont(), 3, 1, GridData.FILL_HORIZONTAL, 0, 0);

        // 1 row
        SWTFactory.createLabel(namecomp, "&Project name:", 1);

        m_appNameText = SWTFactory.createText(namecomp, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY, 1);

        Button browseButton = SWTFactory.createPushButton(namecomp, "Browse...", null);
        browseButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                selectProjectDialog();
            }
        });


        // 2 row
        SWTFactory.createLabel(namecomp, "Platform:", 1);

        m_platformTypeCombo = SWTFactory.createCombo(namecomp, SWT.READ_ONLY, 1, PlatformType.getPublicIds());
        m_platformTypeCombo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateUi();
            }
        });
        m_platformTypeCombo.select(0);

        GridData comboAligment = new GridData();
        comboAligment.horizontalAlignment = GridData.FILL;

        m_platformVersionCombo = SWTFactory.createCombo(namecomp, SWT.READ_ONLY, 1, null);
        m_platformVersionCombo.setLayoutData(comboAligment);
        m_platformVersionCombo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                // TODO: postpone until actual run/debug
                PlatformType platformType = uiPlatformType();
                String version = m_platformVersion.getValue(platformType);

                if (platformType == PlatformType.eAndroid)
                {
                    m_ymlFile.setAndroidVer(version);
                    m_ymlFile.save();
                }
                else if (platformType == PlatformType.eIPhone)
                {
                    m_ymlFile.setIphoneVer(version);
                    m_ymlFile.save();
                }

                updateUi();
            }
        });
        m_platformVersionCombo.select(0);

        m_platformVersion = new PlatformVersion(m_platformVersionCombo);

        SWTFactory.createLabel(namecomp, "Simulator type:", 1);

        m_runTypeCombo = SWTFactory.createCombo(namecomp, SWT.READ_ONLY, 1, RunType.getPublicIds());
        m_runTypeCombo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateUi();
            }
        });
        m_runTypeCombo.select(0);

        SWTFactory.createLabel(namecomp, "", 1);

        // 2 row
        SWTFactory.createLabel(namecomp, "Build:", 1);

        m_buildTypeCombo = SWTFactory.createCombo(namecomp, SWT.READ_ONLY, 1, BuildType.getPublicIds());
        m_buildTypeCombo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateUi();
            }
        });
        m_buildTypeCombo.select(0);

        SWTFactory.createLabel(namecomp, "", 1);

        // 4 row
        m_androidEmuNameLabel = SWTFactory.createLabel(namecomp, "AVD name", 1);

        m_adroidEmuNameText = SWTFactory.createText(namecomp, SWT.BORDER | SWT.SINGLE, 1);
        m_adroidEmuNameText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                String emuName = m_adroidEmuNameText.getText();
                if (m_ymlFile != null && uiPlatformType() == PlatformType.eAndroid)
                {
                    if (emuName.equals(""))
                    {
                        m_ymlFile.removeAndroidEmuName();
                    }
                    else
                    {
                        m_ymlFile.setAndroidEmuName(emuName);
                    }

                    m_ymlFile.save();
                }

                updateUi();
            }
        });

        // 4 row
        GridData checkBoxAligment = new GridData();
        checkBoxAligment.horizontalAlignment = GridData.FILL;
        checkBoxAligment.horizontalSpan = 3;

        m_cleanButton = new Button(composite, SWT.CHECK);
        m_cleanButton.setText("Clean before build");
        m_cleanButton.setSelection(false);
        m_cleanButton.setLayoutData(checkBoxAligment);
        m_cleanButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                updateUi();
            }
        });

        // 5 row
        m_reloadButton = new Button(composite, SWT.CHECK);
        m_reloadButton.setText("Reload application code");
        m_reloadButton.setSelection(false);
        m_reloadButton.setLayoutData(checkBoxAligment);
        m_reloadButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                updateUi();
            }
        });

        // 6 row
        m_traceButton = new Button(composite, SWT.CHECK);
        m_traceButton.setText("Add --trace to command");
        m_traceButton.setSelection(false);
        m_traceButton.setLayoutData(checkBoxAligment);
        m_traceButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                updateUi();
            }
        });
    }

    @Override
    public Control getControl()
    {
        return m_comp;
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration)
    {
        RhodesConfigurationRW rc = new RhodesConfigurationRW(configuration);

        rc.project(m_appNameText.getText());

        rc.platformType(uiPlatformType());
        rc.runType(uiRunType());
        rc.buildType(BuildType.fromPublicId(m_buildTypeCombo.getText()));

        rc.androidVersion(m_platformVersion.getValue(PlatformType.eAndroid));
        rc.iphoneVersion(m_platformVersion.getValue(PlatformType.eIPhone));
        rc.androidEmulator(m_adroidEmuNameText.getText());

        rc.clean(m_cleanButton.getSelection());
        rc.reloadCode(m_reloadButton.getSelection());
        rc.trace(m_traceButton.getSelection());
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
    {
        RhodesConfigurationRW rc = new RhodesConfigurationRW(configuration);

        if (m_selProject == null)
        {
            m_selProject = ProjectFactory.getInstance().getSelectedProject();

            if (m_selProject == null)
            {
                IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

                for (IProject project : allProjects)
                {
                    if (RhodesProject.checkNature(project) || RhoelementsProject.checkNature(project))
                    {
                        m_selProject = project;
                    }
                }
            }
            else
            {
                if (!RhodesProject.checkNature(m_selProject))
                {
                    m_selProject = null;
                }
            }
        }

        String androidVersion = PlatformVersion.getLastValue(PlatformType.eAndroid);
        String androidEmuName = "";
        String iphoneVersion  = PlatformVersion.getLastValue(PlatformType.eIPhone);

        if (m_selProject == null)
        {
            MessageDialog.openInformation(getShell(), "Message", "Create and select rhodes project before create the configuration.");
        }
        else
        {
            rc.project(m_selProject.getName());

            try
            {
                m_ymlFile = AppYmlFile.createFromProject(m_selProject);

                if (m_ymlFile != null)
                {
                    androidVersion = m_ymlFile.getAndroidVer();
                    androidEmuName = m_ymlFile.getAndroidEmuName();
                    iphoneVersion  = m_ymlFile.getIphoneVer();
                    iphoneVersion = iphoneVersion == null ? PlatformVersion.getLastValue(PlatformType.eIPhone) : iphoneVersion;

                }
            }
            catch (FileNotFoundException e)
            {
                MessageDialog.openError(getShell(), "Error", "File build.yml not exists or corrupted. Project - " + getSelectProject().getName());
                e.printStackTrace();
            }
        }

        rc.platformType(PlatformType.eAndroid);
        rc.runType(RunType.eSimulator);
        rc.buildType(BuildType.eLocal);
        rc.androidVersion(androidVersion);
        rc.iphoneVersion(iphoneVersion);
        rc.androidEmulator(androidEmuName);
        rc.clean(false);
        rc.reloadCode(false);
        rc.trace(false);
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration)
    {
        try
        {
            Control scrollParent = getLaunchConfigurationDialog().getActiveTab().getControl().getParent();
            if (scrollParent instanceof ScrolledComposite)
            {
                ((ScrolledComposite)scrollParent).setMinSize(
                    scrollParent.computeSize(minTabSize, SWT.DEFAULT));
            }

            RhodesConfigurationRO rc = new RhodesConfigurationRO(configuration);

            String selProjectName = rc.project();

            if (selProjectName != "")
            {
                m_selProject = ResourcesPlugin.getWorkspace().getRoot().getProject(selProjectName);

                m_appNameText.setText(selProjectName);

                if (m_selProject.isOpen())
                {
                    m_ymlFile = AppYmlFile.createFromProject(m_selProject);
                }
            }

            UiUtils.selectByItem(m_platformTypeCombo, rc.platformType().publicId);
            UiUtils.selectByItem(m_runTypeCombo     , rc.runType     ().publicId);
            UiUtils.selectByItem(m_buildTypeCombo   , rc.buildType   ().publicId);

            m_platformVersion.switchTo(uiPlatformType());
            m_platformVersion.setValue(PlatformType.eAndroid, rc.androidVersion());
            m_platformVersion.setValue(PlatformType.eIPhone, rc.iphoneVersion());
            m_adroidEmuNameText.setText(rc.androidEmulator());

            m_cleanButton.setSelection(rc.clean());
            m_reloadButton.setSelection(rc.reloadCode());
            m_traceButton.setSelection(rc.trace());

            updateUi();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canSave()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Common setting";
    }

    protected IProject getSelectProject()
    {
        return m_selProject;
    }

    private void selectProjectDialog()
    {
        ContainerSelectionDialog dialog = new ContainerSelectionDialog(
            getShell(), ResourcesPlugin.getWorkspace().getRoot(), false, "Select project");

        if (dialog.open() == ContainerSelectionDialog.OK)
        {
            Object[] result = dialog.getResult();

            if (result.length == 1)
            {
                String selProjectName = ((Path) result[0]).toString();
                selProjectName        = selProjectName.replaceAll("/", "");
                IProject selProject   = ResourcesPlugin.getWorkspace().getRoot().getProject(selProjectName);

                if (!RhodesProject.checkNature(selProject) && !RhoelementsProject.checkNature(selProject))
                {
                    MessageDialog.openError(getShell(), "Message", "Project " + selProject.getName() + " is not rhodes application");
                    return;
                }

                try
                {
                    m_ymlFile = AppYmlFile.createFromProject(selProject);
                }
                catch (FileNotFoundException e)
                {
                    MessageDialog.openError(getShell(), "File error", e.toString());
                    e.printStackTrace();
                    return;
                }
                catch (Exception e)
                {
                    MessageDialog.openError(getShell(), "Yaml error", e.toString());
                    e.printStackTrace();
                    return;
                }

                // if yaml file not found or corrupt transaction is revert to prev state
                m_selProject = selProject;
                m_appNameText.setText(selProjectName);

                updateUi();
            }
            else
            {
                MessageDialog.openInformation(getShell(), "Message", "Select single project.");
            }
        }
    }

    private void updateUi()
    {
        UiUtils.updateCombo(m_runTypeCombo, getRunTypePublicIds(uiPlatformType()));
        UiUtils.updateCombo(m_buildTypeCombo,
            getBuildTypePublicIds(uiPlatformType(), uiRunType()));

        m_reloadButton.setVisible(uiRunType() == RunType.eRhoSimulator);

        m_platformVersion.setEnabled(
            (uiPlatformType() == PlatformType.eAndroid && uiRunType() != RunType.eDevice) ||
            uiPlatformType() == PlatformType.eIPhone
        );
        m_platformVersion.switchTo(uiPlatformType());

        boolean visible =
            uiPlatformType() == PlatformType.eAndroid && uiRunType() == RunType.eSimulator;
        m_androidEmuNameLabel.setVisible(visible);
        m_adroidEmuNameText.setVisible(visible);

        getLaunchConfigurationDialog().updateButtons();
    }

    private PlatformType uiPlatformType()
    {
        return PlatformType.fromPublicId(m_platformTypeCombo.getText());
    }

    private RunType uiRunType()
    {
        return RunType.fromPublicId(m_runTypeCombo.getText());
    }


    private String[] getBuildTypePublicIds(PlatformType pt, RunType rt)
    {
        boolean debug =
            getLaunchConfigurationDialog().getMode().equals(ILaunchManager.DEBUG_MODE);

        String map = null;
        switch (pt)
        {
        // 'D' - device, 'S' - simulator, 'R' - rho simulator
        // 'l' - local, 'c' - cloud, '-' - unsupported combination
        //                            debug      run
        //                            DDSSRR     DDSSRR
        //                            lclclc     lclclc
        case eAndroid: map = debug ? "lclcl-" : "lclcl-"; break;
        case eIPhone : map = debug ? "--lcl-" : "--lcl-"; break;
        case eWm     : map = debug ? "----l-" : "lclcl-"; break;
        case eWin32  : map = debug ? "----l-" : "----l-"; break;
        case eWp7    : map = debug ? "----l-" : "l-l-l-"; break;
        }
        assert map != null;

        int i = -1;
        switch (rt)
        {
        case eDevice      : i = 0; break;
        case eSimulator   : i = 2; break;
        case eRhoSimulator: i = 4; break;
        }
        assert i != -1;

        String code = map.substring(i, i + 2);

        List<String> list = new ArrayList<String>();

        for (BuildType bt : BuildType.values())
        {
            if (bt.publicId != null)
            {
                int c = -1;
                switch (bt)
                {
                case eLocal       : c = 'l'; break;
                }
                assert c != -1;

                if (code.indexOf(c) != -1)
                {
                    list.add(bt.publicId);
                }
            }
        }
        return list.toArray(new String[0]);
    }

    private String[] getRunTypePublicIds(PlatformType pt)
    {
        List<String> list = new ArrayList<String>();
        for (RunType rt : RunType.values())
        {
            if (rt.publicId != null && getBuildTypePublicIds(pt, rt).length != 0)
            {
                list.add(rt.publicId);
            }
        }
        return list.toArray(new String[0]);
    }
}
