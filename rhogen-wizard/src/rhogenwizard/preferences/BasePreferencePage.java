package rhogenwizard.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbenchPreferencePage;

public abstract class BasePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage 
{
	protected BasePreferencePage(int style) 
	{
		super(style);
	}
}
