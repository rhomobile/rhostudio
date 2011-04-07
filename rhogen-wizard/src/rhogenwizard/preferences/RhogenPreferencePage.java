package rhogenwizard.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import rhogenwizard.Activator;
import rhogenwizard.buildfile.SdkYmlAdapter;
import rhogenwizard.buildfile.SdkYmlFile;
import rhogenwizard.buildfile.YmlFile;

public class RhogenPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage 
{
	public RhogenPreferencePage() 
	{
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Rhodes rhobuild.yml preferences");
	}
	
	@Override
	public boolean performOk()
	{
		boolean ret = super.performOk();

		try 
		{
			String javaPath   = getPreferenceStore().getString(PreferenceConstants.JAVA_PATH);
			String sdkPath    = getPreferenceStore().getString(PreferenceConstants.ANDROID_SDK_PATH);
			String ndkPath    = getPreferenceStore().getString(PreferenceConstants.ANDROID_NDK_PATH);
			String cabWizPath = getPreferenceStore().getString(PreferenceConstants.CAB_WIZARD_PATH);
			
			SdkYmlFile ymlFile = SdkYmlAdapter.getRhobuildFile();
		
			ymlFile.setAndroidNdkPath(ndkPath);
			ymlFile.setCabWizPath(cabWizPath);
			ymlFile.setAndroidSdkPath(sdkPath);
			ymlFile.setJavaPath(javaPath);
			
			ymlFile.save();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

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
		//addField(new DirectoryFieldEditor(PreferenceConstants.BUILDFILE_PATH, 
		//		"&Rhodes folder:", getFieldEditorParent()));
		
		addField(new DirectoryFieldEditor(PreferenceConstants.JAVA_PATH, 
				"&Java path:", getFieldEditorParent()));

		addField(new DirectoryFieldEditor(PreferenceConstants.ANDROID_SDK_PATH, 
				"&Android SDK:", getFieldEditorParent()));
		
		addField(new DirectoryFieldEditor(PreferenceConstants.ANDROID_NDK_PATH, 
				"&Android NDK:", getFieldEditorParent()));

		addField(new DirectoryFieldEditor(PreferenceConstants.CAB_WIZARD_PATH, 
				"&Cab wizard path:", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) 
	{
		PreferenceInitializer pInit = new PreferenceInitializer();
		pInit.initializeDefaultPreferences();
	}	
}