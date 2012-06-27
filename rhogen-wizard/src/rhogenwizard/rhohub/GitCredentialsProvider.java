package rhogenwizard.rhohub;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class GitCredentialsProvider extends CredentialsProvider
{
    @Override
    public boolean get(URIish arg0, CredentialItem... arg1) throws UnsupportedCredentialItem
    {
        InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), 
            "Git password", "Please enter you git password.", "", null) 
        {
            @Override
            protected int getInputTextStyle() 
            {
                return SWT.SINGLE | SWT.BORDER ;
            }
        };
        dlg.open();
        
        CredentialItem.StringType pwdCred = (CredentialItem.StringType )arg1[0];
        pwdCred.setValue(dlg.getValue());
        
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
