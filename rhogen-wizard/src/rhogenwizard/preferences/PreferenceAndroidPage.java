package rhogenwizard.preferences;

import org.eclipse.ui.IWorkbench;

import rhogenwizard.Activator;
import rhogenwizard.constants.MsgConstants;

public class PreferenceAndroidPage extends BasePreferencePage 
{
	PreferenceInitializer m_pInit = null;
	
	public PreferenceAndroidPage() 
	{
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(MsgConstants.preferencesPageTitle);
	}
	
	@Override
	public boolean performOk()
	{
		boolean ret = super.performOk();
		m_pInit.savePreferences();
		return ret;
	}

	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() 
	{
		checkRhodesSdk();
		
		addField(new DirectoryFieldEditor(PreferenceConstants.androidSdkParh, 
				"&Android SDK:", getFieldEditorParent()));
		
		addField(new DirectoryFieldEditor(PreferenceConstants.androidNdkPath, 
				"&Android NDK:", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) 
	{
		m_pInit = PreferenceInitializer.getInstance();
	}	
}