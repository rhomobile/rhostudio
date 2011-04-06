package rhogenwizard.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import rhogenwizard.Activator;
import rhogenwizard.buildfile.SdkYmlAdapter;
import rhogenwizard.buildfile.SdkYmlFile;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer 
{	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() 
	{
		try 
		{
			SdkYmlFile ymlFile = SdkYmlAdapter.getRhobuildFile();
			
			if (ymlFile != null)
			{
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				
				String cabWizPath = ymlFile.getCabWizPath() != null ? ymlFile.getCabWizPath() : "";
				String androidSdkPath =  ymlFile.getAndroidSdkPath() != null ? ymlFile.getAndroidSdkPath() : "";
				String androidNdkPath = ymlFile.getAndroidNdkPath() != null ? ymlFile.getAndroidNdkPath() : "";
				String javaPath  = ymlFile.getJavaPath() != null ? ymlFile.getJavaPath() : "";
		
				store.setValue(PreferenceConstants.JAVA_PATH, javaPath);
				store.setValue(PreferenceConstants.ANDROID_SDK_PATH, androidSdkPath);
				store.setValue(PreferenceConstants.ANDROID_NDK_PATH, androidNdkPath);
				store.setValue(PreferenceConstants.CAB_WIZARD_PATH, cabWizPath);
			}			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
