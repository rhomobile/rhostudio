package rhogenwizard.rhohub;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.preferences.PreferenceInitializer;
import rhogenwizard.sdk.task.RubyExecTask;

public class TokenChecker
{
    private static class Dialog extends TitleAreaDialog
    {
        private final String m_message;
        private final int    m_messageType;
        private Text   m_emailText;
        private Text   m_passwordText;
        private String m_email;
        private String m_password;

        /*
         * messageType should be one of:
         * IMessageProvider.NONE
         * IMessageProvider.INFORMATION
         * IMessageProvider.WARNING
         * IMessageProvider.ERROR
         */
        public Dialog(String message, int messageType)
        {
            super(null);
            m_message = message;
            m_messageType = messageType;
        }

        @Override
        public void create()
        {
            super.create();
            setTitle("Rhomobile.com login");
            setMessage(m_message, m_messageType);
        }

        @Override
        protected Control createDialogArea(Composite parent)
        {
            Composite area = (Composite) super.createDialogArea(parent);

            Composite container = new Composite(area, SWT.NONE);
            container.setLayoutData(new GridData(GridData.FILL_BOTH));
            container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            container.setLayout(new GridLayout(2, false));

            m_emailText = addText(container, "Email", SWT.NONE);
            m_passwordText = addText(container, "Password", SWT.PASSWORD);
            addLink(container,
                "Don't have an account? <a>Signup</a>", "http://rhomobile.com/signup");

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
            m_email = m_emailText.getText();
            m_password = m_passwordText.getText();
            super.okPressed();
        }

        public String getEmail()
        {
            return m_email;
        }

        public String getPassword()
        {
            return m_password;
        }

        private Text addText(Composite container, String prompt, int style)
        {
            Label label = new Label(container, SWT.RIGHT);
            label.setText(prompt + ": ");

            Text text = new Text(container, SWT.SINGLE | SWT.BORDER | style);

            GridData gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            gridData.horizontalAlignment = GridData.FILL;
            text.setLayoutData(gridData);

            return text;
        }

        private void addLink(Composite container, String text, final String url)
        {
            new Label(container, SWT.NONE);

            Link link = new Link(container, SWT.NONE);
            link.setText(text);
            link.addSelectionListener(new SelectionListener()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    open();
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e)
                {
                    open();
                }

                private void open()
                {
                    try
                    {
                        PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(
                            url));
                    }
                    catch (PartInitException ex)
                    {
                        ex.printStackTrace();
                    }
                    catch (MalformedURLException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            });

            GridData gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            gridData.horizontalAlignment = GridData.FILL;
            link.setLayoutData(gridData);
        }
    }

    private static class CheckLicenseException extends Exception
    {
        private static final long serialVersionUID = 1L;
    }

    private static class Answer
    {
        public boolean ok = false;
        public String  email;
        public String  password;
    }

    public static boolean login(String workDir, String firstMessage)
    {
        String message = firstMessage;
        int messageType =
            (firstMessage == null) ? IMessageProvider.NONE : IMessageProvider.INFORMATION;
        try
        {
            do
            {
                Answer answer = login(message, messageType);

                if (!answer.ok)
                {
                    return false;
                }

                RhoHubCommands.login(workDir, answer.email, answer.password);

                message = "Your credentials aren't valid. Please try again.";
                messageType = IMessageProvider.WARNING;
            }
            while (!checkLicense(workDir));
        }
        catch (CheckLicenseException e)
        {
            return false;
        }

        return true;
    }

    public static boolean processToken(IProject project)
    {
        return processToken(project.getLocation().toOSString());
    }

    public static boolean processToken()
    {
        return processToken(PreferenceInitializer.getRhodesPath());
    }

    private static boolean processToken(String workDir)
    {
        try
        {
            return checkLicense(workDir) ||
                login(workDir, "You must be logged in to RhoMobile.com to build.");
        }
        catch (CheckLicenseException e)
        {
            return false;
        }
    }

    private static Answer login(final String message, final int messageType)
    {
        final Answer answer = new Answer();

        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                Dialog dialog = new Dialog(message, messageType);
                dialog.create();

                if (dialog.open() == Window.OK)
                {
                    answer.ok = true;
                    answer.email = dialog.getEmail();
                    answer.password = dialog.getPassword();
                }
            }
        });

        return answer;
    }

    private static boolean checkLicense(String workDir) throws CheckLicenseException
    {
        RubyExecTask task =
            new RubyExecTask(workDir, SysCommandExecutor.RUBY_BAT, "rake", "token:check");
        task.run();
        if (!task.isOk())
        {
            throw new CheckLicenseException();
        }
        return Arrays.asList(task.getOutput().split("\n|\r")).contains("TokenValid[YES]");
    }
}
