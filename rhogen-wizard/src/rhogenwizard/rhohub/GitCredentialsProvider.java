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
	private static String gitTitle = "Git";
	
    @Override
    public boolean get(URIish arg0, final CredentialItem... arg1) throws UnsupportedCredentialItem
    {
    	for (CredentialItem item : arg1)
    	{
	    	final CredentialItem inItem = item;

//	    	if (inItem instanceof CredentialItem.Password)
//	    	{
//	    		CredentialItem.Password pwdItem = (CredentialItem.Password)inItem;
//	    		
//	    		pwdItem.s
//	    		inItem
//	    	}
	    	if (inItem instanceof CredentialItem.InformationalMessage)
	    	{
	    		infoProcess(inItem);
	    	}
	    	if (inItem instanceof CredentialItem.YesNoType)
	    	{
	    		yesnoTypeProcess(inItem);
	    	}
	    	else if (inItem instanceof CredentialItem.StringType)
	    	{
		    	stringTypeProcess(inItem);
	    	}
    	}
    	
        return true;
    }

	private void infoProcess(final CredentialItem inItem) 
	{
		CredentialItem.InformationalMessage msgCred = (CredentialItem.InformationalMessage)inItem;
		
		DialogUtils.information(gitTitle, msgCred.getPromptText());
	}

	private void yesnoTypeProcess(final CredentialItem inItem) 
	{
		CredentialItem.YesNoType yesnoCred = (CredentialItem.YesNoType)inItem;
		
		yesnoCred.setValue(DialogUtils.quetsion(gitTitle, inItem.getPromptText()));
	}

	private void stringTypeProcess(final CredentialItem inItem) 
	{
		Display.getDefault().syncExec(new Runnable() 
		{
			public void run() 
			{
		        InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), 
		        		gitTitle, inItem.getPromptText(), "", null) 
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
		        
		        CredentialItem.StringType pwdCred = (CredentialItem.StringType)inItem;
		        pwdCred.setValue(dlg.getValue() == null ? "" : dlg.getValue());
			}
		});
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
