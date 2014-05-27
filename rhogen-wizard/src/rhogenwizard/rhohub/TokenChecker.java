package rhogenwizard.rhohub;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;
import rhogenwizard.sdk.task.RunTask;
import rhogenwizard.sdk.task.rhohub.TokenTask;

class TokenCheckDialog extends TitleAreaDialog 
{
	private Text m_tokenText;

	private String m_strToken;

	public TokenCheckDialog(Shell parentShell) 
	{
		super(parentShell);
	}

	@Override
	public void create() 
	{
		super.create();
		setTitle("Setup token dialog");
		setMessage("Dialog for setup new token.", IMessageProvider.INFORMATION);
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
		tokenLabel.setText("Token: ");

		GridData dataFirstName = new GridData();
		dataFirstName.grabExcessHorizontalSpace = true;
		dataFirstName.horizontalAlignment = GridData.FILL;

		m_tokenText = new Text(container, SWT.BORDER);
		m_tokenText.setLayoutData(dataFirstName);
	}

	@Override
	protected boolean isResizable() 
	{
		return true;
	}

	private void saveInput() 
	{
		m_strToken = m_tokenText.getText();
	}

	@Override
	protected void okPressed() 
	{
		saveInput();
		super.okPressed();
	}

	public String getTokenString()
	{
		return m_strToken;
	}
}

public class TokenChecker
{
	public static boolean processToken(final String workDir)
	{
		if (!checkRhoHubLicense(workDir))
		{
	        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
	        {
	            @Override
	            public void run()
	            {
	    			TokenCheckDialog dialog = new TokenCheckDialog(null);
	    			dialog.create();

	    			if (dialog.open() == Window.OK) 
	    			{    				
	    				TokenTask.setToken(workDir, dialog.getTokenString());
	    			} 	
	            }
	        });
		}
		
		if (!checkRhoHubLicense(workDir))
		{
			//DialogUtils.error("Wrong token string.", "This token string is invalid. Re-run the build and try to enter token again.");
			return false;
		}		
		
		return true;
	}
	
    public static boolean checkRhoHubLicense(String workDir)
    {
        RunTask task = new RubyExecTask(workDir, SysCommandExecutor.RUBY_BAT, "rake", "token:check");
        task.run();
        return task.isOk();
    }
}