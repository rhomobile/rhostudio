package rhogenwizard.preferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import rhogenwizard.Activator;
import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.buildfile.SdkYmlAdapter;
import rhogenwizard.buildfile.SdkYmlFile;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer 
{	
	static PreferenceInitializer m_initPref = null;
	
	private String       m_defaultBbVer = null;
	private SdkYmlFile   m_ymlFile = null;
	private List<String> m_bbVers = null;
	private IProject     m_currProject = null;
	private String       m_currRhodesPath = null;
	
	static PreferenceInitializer getInstance()
	{
		try 
		{
			if (m_initPref != null)
			{
				//if (m_initPref.m_currProject == null || m_initPref.isRhodesPathChanged())
				//{
					m_initPref.initFromFirstProject();
				//}
				
				return m_initPref;
			}
			
			m_initPref = new PreferenceInitializer();
		
			m_initPref.initFromFirstProject();

		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return m_initPref;
	}
	
	private void initFromFirstProject() 
	{
		List<String> projectNames = getRhodesProjects();
		
		if (projectNames != null && projectNames.size() > 0)
		{
			String firstProject = projectNames.get(0);
			initFromProject(firstProject);
		}
	}

	public SdkYmlFile getYmlFile()
	{
		return m_ymlFile;
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
				String androidSdkPath = m_ymlFile.getAndroidSdkPath() != null ? m_ymlFile.getAndroidSdkPath() : "";
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
	
	public void savePreferences()
	{
		try 
		{
			if (m_ymlFile != null)
			{
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				
				String cabWizPath    = store.getString(PreferenceConstants.CAB_WIZARD_PATH);
				String javaPath      = store.getString(PreferenceConstants.JAVA_PATH);
				String sdkPath       = store.getString(PreferenceConstants.ANDROID_SDK_PATH);
				String ndkPath       = store.getString(PreferenceConstants.ANDROID_NDK_PATH);
				String bbVersionName = store.getString(PreferenceConstants.BB_VERSION_NAME);
				String bbJdkPath     = store.getString(PreferenceConstants.BB_JDK_PATH);
				String bbMdsPath     = store.getString(PreferenceConstants.BB_MDS_PATH);
				String bbSim         = store.getString(PreferenceConstants.BB_SIM);
				
				m_ymlFile.setJavaPath(javaPath);
				m_ymlFile.setCabWizPath(cabWizPath);
				m_ymlFile.setAndroidNdkPath(ndkPath);
				m_ymlFile.setAndroidSdkPath(sdkPath);
				m_ymlFile.setBbJdkPath(bbVersionName, bbJdkPath);
				m_ymlFile.setBbMdsPath(bbVersionName, bbMdsPath);
				m_ymlFile.setBbSimPort(bbVersionName, new Integer(bbSim));
				
				m_ymlFile.save();
			}			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private File getAppSdkCongigPath(IProject project)
	{
		String configPath = project.getLocation() + File.separator + AppYmlFile.configFileName;
		File cfgFile = new File(configPath);
		return cfgFile;
	}

	public List<String> getRhodesProjects() 
	{
		List<String> namesList = new ArrayList<String>();
		
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		
		for(IProject p : projects)
		{
			File cfgFile = getAppSdkCongigPath(p);
			
			if (cfgFile.exists())
			{
				namesList.add(p.getName());
			}
		}
		
		return namesList;
	}

	public void initFromProject(String projectName) 
	{
		try 
		{
			m_currProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			
			AppYmlFile appYmlFile = AppYmlFile.createFromProject(m_currProject);
			m_currRhodesPath  = appYmlFile.getSdkConfigPath();
			
			m_ymlFile = new SdkYmlFile(m_currRhodesPath );
			
			m_bbVers       = m_ymlFile.getBbVersions();
			m_defaultBbVer = m_bbVers.get(0);
			
			initializeDefaultPreferences();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	private boolean isRhodesPathChanged()
	{
		try 
		{
			AppYmlFile appYmlFile;
			
			appYmlFile = AppYmlFile.createFromProject(m_currProject);
			
			String sdkPath = appYmlFile.getSdkConfigPath();
			
			if (m_currRhodesPath != null)
			{
				return !sdkPath.equals(m_currRhodesPath);
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		return false;
	}
}
