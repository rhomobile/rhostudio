package rhogenwizard.preferences;

import java.io.FileNotFoundException;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import rhogenwizard.Activator;
import rhogenwizard.ConsoleHelper;
import rhogenwizard.buildfile.SdkYmlAdapter;
import rhogenwizard.buildfile.SdkYmlFile;
import rhogenwizard.buildfile.YmlFile;

public class RhogenPreferenceAndroidPage extends BasePreferencePage 
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
		checkRhodesSdk();
		
		addField(new RhogenDirectoryFieldEditor(PreferenceConstants.androidSdkParh, 
				"&Android SDK:", getFieldEditorParent()));
		
		addField(new RhogenDirectoryFieldEditor(PreferenceConstants.androidNdkPath, 
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