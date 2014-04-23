package rhogenwizard.preferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import rhogenwizard.Activator;
import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.buildfile.SdkYmlFile;
import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.sdk.task.rhohub.TokenTask;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer 
{	
    static String                rhodesDefaultVersion = "3.3.2";
	static PreferenceInitializer initPref = null;
	
	private String       m_defaultBbVer   = null;
	private SdkYmlFile   m_ymlFile        = null;
	private List<String> m_bbVers         = null;
	private IProject     m_currProject    = null;
	private IPath        m_currRhodesPath = null;
	private String       m_rhohubToken    = null;  
			
	public static PreferenceInitializer getInstance()
	{
		try 
		{
			if (initPref != null)
			{
				initPref.initFromFirstProject();
				
				return initPref;
			}
			
			initPref = new PreferenceInitializer();
		
			initPref.initFromFirstProject();
		}
		catch (Exception e) 
		{
		    Activator.logError(e);
		}
		
		return initPref;
	}
	
	private void initFromFirstProject() 
	{
		List<String> projectNames = getRhodesProjects();

		if (projectNames == null)
			return;

		for (String projectName : projectNames)
		{
			IProject currProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			
			if (AppYmlFile.isExists(currProject.getLocation().toOSString()))
			{
				initFromProject(projectName);
				return;
			}
		}
	}
	
	private File getAppSdkCongigPath(IProject project)
	{
		String configPath = project.getLocation() + File.separator + AppYmlFile.configFileName;
		File cfgFile = new File(configPath);
		return cfgFile;
	}
	
	private void initFromProject(String projectName) 
	{
		try 
		{
			m_currProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			
			if (m_currProject.isOpen())
			{
				AppYmlFile appYmlFile = AppYmlFile.createFromProject(m_currProject);
				m_currRhodesPath      = new Path(appYmlFile.getSdkConfigPath());
				
				if (!m_currRhodesPath.isAbsolute())
				{
					IPath basePath = new Path(m_currProject.getLocation().toOSString());
					m_currRhodesPath = basePath.append(m_currRhodesPath); 
				}
				
				m_ymlFile = new SdkYmlFile(m_currRhodesPath.toFile().getAbsolutePath());
				
				m_bbVers  = m_ymlFile.getBbVersions();
				
				if (m_bbVers.size() == 0)
					m_defaultBbVer = "";
				else
					m_defaultBbVer = m_bbVers.get(0);
				
				initializeDefaultPreferences();
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
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
	
	public void initializeDefaultPreferences() 
	{
	    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	    
		try 
		{
			if (m_ymlFile != null)
			{
				String cabWizPath     = m_ymlFile.getCabWizPath() != null ? m_ymlFile.getCabWizPath() : "";
				String vcbuildPath    = m_ymlFile.getVcBuildPath() != null ? m_ymlFile.getVcBuildPath() : "";
				String androidSdkPath = m_ymlFile.getAndroidSdkPath() != null ? m_ymlFile.getAndroidSdkPath() : "";
				String androidNdkPath = m_ymlFile.getAndroidNdkPath() != null ? m_ymlFile.getAndroidNdkPath() : "";
				String javaPath       = m_ymlFile.getJavaPath() != null ? m_ymlFile.getJavaPath() : "";
				String bbJdkPath      = m_ymlFile.getBbJdkPath(m_defaultBbVer) != null ? m_ymlFile.getBbJdkPath(m_defaultBbVer) : "";
				String bbMdsPath      = m_ymlFile.getBbMdsPath(m_defaultBbVer) != null ? m_ymlFile.getBbMdsPath(m_defaultBbVer) : "";
				String bbSimPort      = m_ymlFile.getBbSimPort(m_defaultBbVer) != null ? m_ymlFile.getBbSimPort(m_defaultBbVer) : "";
			
				if (m_rhohubToken == null)
				{
					m_rhohubToken = TokenTask.getToken(m_currProject.getLocation().toOSString());
				}
				
				store.setDefault(PreferenceConstants.bbVersionName, m_defaultBbVer);
				store.setDefault(PreferenceConstants.bbJdkPath, bbJdkPath);
				store.setDefault(PreferenceConstants.bbMdsPath, bbMdsPath);
				store.setDefault(PreferenceConstants.bbSim, bbSimPort);
				store.setDefault(PreferenceConstants.javaPath, javaPath);
				store.setDefault(PreferenceConstants.androidSdkParh, androidSdkPath);
				store.setDefault(PreferenceConstants.androidNdkPath, androidNdkPath);
				store.setDefault(PreferenceConstants.cabWizardPath, cabWizPath);
				store.setDefault(PreferenceConstants.vcBuildPath, vcbuildPath);
				store.setDefault(IRhoHubSetting.rhoHubToken, m_rhohubToken);
				
				store.setValue(PreferenceConstants.bbVersionName, m_defaultBbVer);
				store.setValue(PreferenceConstants.bbJdkPath, bbJdkPath);
				store.setValue(PreferenceConstants.bbMdsPath, bbMdsPath);
				store.setValue(PreferenceConstants.bbSim, bbSimPort);
				store.setValue(PreferenceConstants.javaPath, javaPath);
				store.setValue(PreferenceConstants.androidSdkParh, androidSdkPath);
				store.setValue(PreferenceConstants.androidNdkPath, androidNdkPath);
				store.setValue(PreferenceConstants.cabWizardPath, cabWizPath);
				store.setValue(PreferenceConstants.vcBuildPath, vcbuildPath);
				store.setValue(IRhoHubSetting.rhoHubToken, m_rhohubToken);				
			}			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		store.setDefault(IRhoHubSetting.rhoHubUrl, "https://app.rhohub.com/api/v1");		
		store.setDefault(IRhoHubSetting.rhoHubProxy, "");
		store.setDefault(IRhoHubSetting.rhoHubSelectedRhodesVesion, rhodesDefaultVersion);	
	}
	
	public void savePreferences()
	{
		try 
		{
			if (m_ymlFile != null)
			{
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				
				String cabWizPath    = store.getString(PreferenceConstants.cabWizardPath);
				String vcbuildPath   = store.getString(PreferenceConstants.vcBuildPath);
				String javaPath      = store.getString(PreferenceConstants.javaPath);
				String sdkPath       = store.getString(PreferenceConstants.androidSdkParh);
				String ndkPath       = store.getString(PreferenceConstants.androidNdkPath);
				String bbVersionName = store.getString(PreferenceConstants.bbVersionName);
				String bbJdkPath     = store.getString(PreferenceConstants.bbJdkPath);
				String bbMdsPath     = store.getString(PreferenceConstants.bbMdsPath);
				String bbSim         = store.getString(PreferenceConstants.bbSim);
				String hubToken      = store.getString(IRhoHubSetting.rhoHubToken);
				
				TokenTask.setToken(m_currProject.getLocation().toOSString(), hubToken);
				
				m_ymlFile.setJavaPath(javaPath);
				m_ymlFile.setCabWizPath(cabWizPath);
				m_ymlFile.setVcBuildPath(vcbuildPath);
				m_ymlFile.setAndroidNdkPath(ndkPath);
				m_ymlFile.setAndroidSdkPath(sdkPath);		
				m_ymlFile.setBbJdkPath(bbVersionName, bbJdkPath);
				m_ymlFile.setBbMdsPath(bbVersionName, bbMdsPath);
				
				if (bbSim.length() != 0)
					m_ymlFile.setBbSimPort(bbVersionName, new Integer(bbSim));
				else
					m_ymlFile.setBbSimPort(bbVersionName, 0);
				
				m_ymlFile.save();
			}			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public String getRhodesPath()
	{
		return m_currRhodesPath.toFile().getParentFile().toString();
	}
	
	public List<String> getRhodesProjects() 
	{
		List<String> namesList = new ArrayList<String>();
		
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		
		for(IProject p : projects)
		{
			if (p.isOpen())
			{
				File cfgFile = getAppSdkCongigPath(p);
				
				if (cfgFile.exists())
				{
					namesList.add(p.getName());
				}
			}
		}
			
		return namesList;
	}
}
