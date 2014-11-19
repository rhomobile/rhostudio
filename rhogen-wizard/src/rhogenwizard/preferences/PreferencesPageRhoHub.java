package rhogenwizard.preferences;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import rhogenwizard.Activator;
import rhogenwizard.rhohub.RhoHubCommands;
import rhogenwizard.rhohub.TokenChecker;

public class PreferencesPageRhoHub extends BasePreferencePage
{
    private PreferenceInitializer m_pInit = null;
    private Label m_messageLabel = null;

    public PreferencesPageRhoHub()
    {
        super(GRID);
        noDefaultAndApplyButton();
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
                new Job("Logging in...") {
                    @Override
                    protected IStatus run(IProgressMonitor monitor)
                    {
                        showMessage("Please wait...");
                        String rhodesPath = PreferenceInitializer.getRhodesPath();
                        RhoHubCommands.logout(rhodesPath);
                        showMessage(TokenChecker.login(rhodesPath, null) ?
                            "You are logged in to rhomobile.com." :
                            "You aren't logged in to rhomobile.com."
                        );
                        return Status.OK_STATUS;
                    }
                }.schedule();
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
                new Job("Logging out...") {
                    @Override
                    protected IStatus run(IProgressMonitor monitor)
                    {
                        showMessage("Please wait...");
                        RhoHubCommands.logout(PreferenceInitializer.getRhodesPath());
                        showMessage("You are logged out.");
                        return Status.OK_STATUS;
                    }
                }.schedule();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        });

        m_messageLabel = new Label(parent, SWT.NONE);

        return new Composite(parent, SWT.NULL);
    }

    public void init(IWorkbench workbench)
    {
        m_pInit = PreferenceInitializer.getInstance();
    }

    private void showMessage(final String message)
    {
        final Label messageLabel = m_messageLabel;
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (messageLabel != null)
                {
                    messageLabel.setText(message);
                    messageLabel.getParent().layout();
                }
            }
        });
    }
}
