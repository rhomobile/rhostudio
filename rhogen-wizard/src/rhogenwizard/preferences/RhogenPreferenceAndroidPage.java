package rhogenwizard.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import rhogenwizard.Activator;
import rhogenwizard.buildfile.SdkYmlAdapter;
import rhogenwizard.buildfile.SdkYmlFile;
import rhogenwizard.buildfile.YmlFile;

public class RhogenPreferenceAndroidPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage 
{
	PreferenceInitializer m_pInit = null;
	
	public RhogenPreferenceAndroidPage() 
	{
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Rhodes rhobuild.yml preferences");
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
		addField(new RhogenDirectoryFieldEditor(PreferenceConstants.ANDROID_SDK_PATH, 
				"&Android SDK:", getFieldEditorParent()));
		
		addField(new RhogenDirectoryFieldEditor(PreferenceConstants.ANDROID_NDK_PATH, 
				"&Android NDK:", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) 
	{
		m_pInit = PreferenceInitializer.getInstance();
	}	
}