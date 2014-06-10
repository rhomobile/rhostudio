package rhogenwizard.preferences;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IWorkbench;

import rhogenwizard.Activator;
import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.rhohub.RhoHubCommands;
import rhogenwizard.rhohub.TokenChecker;

public class PreferencesPageRhoHub extends BasePreferencePage
{
    private PreferenceInitializer m_pInit = null;

    public PreferencesPageRhoHub()
    {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription("");
    }

    @Override
    public boolean performOk()
    {
        boolean ret = super.performOk();

        try
        {
            m_pInit.savePreferences();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return ret;
    }

    public void createFieldEditors()
    {
        checkRhodesSdk();
    }

    @Override
    protected Control createContents(Composite parent)
    {
        parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        GridData checkBoxAligment = new GridData();
        checkBoxAligment.horizontalAlignment = GridData.FILL;
        checkBoxAligment.horizontalSpan = 3;

        Button loginButton = new Button(parent, SWT.NONE);
        loginButton.setText("Login rhomobile.com");
        loginButton.setLayoutData(checkBoxAligment);
        loginButton.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                String rhodesPath = m_pInit.getRhodesPath();
                RhoHubCommands.logout(rhodesPath);
                TokenChecker.login(rhodesPath);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        });

        Button logoutButton = new Button(parent, SWT.NONE);
        logoutButton.setText("Logout rhomobile.com");
        logoutButton.setLayoutData(checkBoxAligment);
        logoutButton.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                RhoHubCommands.logout(m_pInit.getRhodesPath());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        });

        //return parent;
        return new Composite(parent, SWT.NULL);
    }

    public void init(IWorkbench workbench)
    {
        m_pInit = PreferenceInitializer.getInstance();
    }
}