package rhogenwizard;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

public class DialogUtils
{
    public static void warn(final String title, final String message)
    {
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                MessageDialog.openWarning(null, title, message);
            }
        });
    }
    
    public static boolean confirm(final String title, final String message)
    {
        final boolean ok[] = new boolean[1];

        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                ok[0] = MessageDialog.openConfirm(null, title, message);
            }
        });

        return ok[0];
    }
}
