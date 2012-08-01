package rhogenwizard.rhohub;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;

import rhogenwizard.DialogUtils;

public class GitCredentialsProvider extends CredentialsProvider
{
    @Override
    public boolean get(URIish arg0, final CredentialItem... arg1) throws UnsupportedCredentialItem
    {
    	if (arg1[0] instanceof CredentialItem.YesNoType)
    	{
    		CredentialItem.YesNoType yesnoCred = (CredentialItem.YesNoType)arg1[0];
    		
    		yesnoCred.setValue(DialogUtils.quetsion("Git", arg1[0].getPromptText()));
    	}
    	else if (arg1[0] instanceof CredentialItem.StringType)
    	{
	    	Display.getDefault().syncExec(new Runnable() 
	    	{
	    		public void run() 
	    		{
			        InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), 
			            "Git password", "Please enter you git password:", "", null) 
			        {
			            @Override
			            protected int getInputTextStyle() 
			            {
			                return SWT.SINGLE | SWT.BORDER | SWT.PASSWORD;
			            }
			        };
			        
			        if (dlg.open() == Window.CANCEL)
			        {
			        	throw new SWTException(SWT.ERROR_FAILED_EXEC);
			        }
			        
			        CredentialItem.StringType pwdCred = (CredentialItem.StringType)arg1[0];
			        pwdCred.setValue(dlg.getValue() == null ? "" : dlg.getValue());
	    		}
	    	});
    	}
    	
        return true;
    }

    @Override
    public boolean isInteractive()
    {
        return false;
    }

    @Override
    public boolean supports(CredentialItem... arg0)
    {
        return false;
    }
}
