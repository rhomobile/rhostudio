package rhogenwizard.preferences;

import java.util.List;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import rhogenwizard.constants.MsgConstants;

public abstract class BasePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage 
{
	protected BasePreferencePage(int style) 
	{
		super(style);
	}
	
	void checkRhodesSdk()
	{
		List<String> projNames = PreferenceInitializer.getInstance().getRhodesProjects();

		if (projNames == null || projNames.size() == 0)
		{
			MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK); 
			messageBox.setText("Error");
			messageBox.setMessage(MsgConstants.errNotFoundRhodesSdk);
			messageBox.open();				
		}
	}
}
