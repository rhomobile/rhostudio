package rhogenwizard.preferences;

import java.util.List;

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
	private String       m_defaultBbVer = null;
	private SdkYmlFile   m_ymlFile = null;
	private List<String> m_bbVers = null;
	
	public PreferenceInitializer()
	{
		try 
		{
			m_ymlFile      = SdkYmlAdapter.getRhobuildFile();
			m_bbVers       = m_ymlFile.getBbVersions();
			m_defaultBbVer = m_bbVers.get(0);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public List<String> getBbVersions()
	{
		return m_bbVers;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() 
	{
		try 
		{
			if (m_ymlFile != null)
			{
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				
				String cabWizPath     = m_ymlFile.getCabWizPath() != null ? m_ymlFile.getCabWizPath() : "";
				String androidSdkPath =  m_ymlFile.getAndroidSdkPath() != null ? m_ymlFile.getAndroidSdkPath() : "";
				String androidNdkPath = m_ymlFile.getAndroidNdkPath() != null ? m_ymlFile.getAndroidNdkPath() : "";
				String javaPath       = m_ymlFile.getJavaPath() != null ? m_ymlFile.getJavaPath() : "";
				String bbJdkPath      = m_ymlFile.getBbJdkPath(m_defaultBbVer) != null ? m_ymlFile.getBbJdkPath(m_defaultBbVer) : "";
				String bbMdsPath      = m_ymlFile.getBbMdsPath(m_defaultBbVer) != null ? m_ymlFile.getBbMdsPath(m_defaultBbVer) : "";
				String bbSimPort      = m_ymlFile.getBbSimPort(m_defaultBbVer) != null ? m_ymlFile.getBbSimPort(m_defaultBbVer) : "";
			
				store.setDefault(PreferenceConstants.BB_VERSION_NAME, m_defaultBbVer);
				store.setDefault(PreferenceConstants.BB_JDK_PATH, bbJdkPath);
				store.setDefault(PreferenceConstants.BB_MDS_PATH, bbMdsPath);
				store.setDefault(PreferenceConstants.BB_SIM, bbSimPort);
				store.setDefault(PreferenceConstants.JAVA_PATH, javaPath);
				store.setDefault(PreferenceConstants.ANDROID_SDK_PATH, androidSdkPath);
				store.setDefault(PreferenceConstants.ANDROID_NDK_PATH, androidNdkPath);
				store.setDefault(PreferenceConstants.CAB_WIZARD_PATH, cabWizPath);
				
				store.setValue(PreferenceConstants.BB_VERSION_NAME, m_defaultBbVer);
				store.setValue(PreferenceConstants.BB_JDK_PATH, bbJdkPath);
				store.setValue(PreferenceConstants.BB_MDS_PATH, bbMdsPath);
				store.setValue(PreferenceConstants.BB_SIM, bbSimPort);
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
