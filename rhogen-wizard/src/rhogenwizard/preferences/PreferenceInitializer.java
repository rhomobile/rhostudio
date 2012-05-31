package rhogenwizard.preferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.management.monitor.Monitor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import rhogenwizard.Activator;
import rhogenwizard.ConsoleHelper;
import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.buildfile.SdkYmlAdapter;
import rhogenwizard.buildfile.SdkYmlFile;
import rhogenwizard.constants.CommonConstants;

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
				m_initPref.initFromFirstProject();
				
				return m_initPref;
			}
			
			m_initPref = new PreferenceInitializer();
		
			m_initPref.initFromFirstProject();
		}
		catch (Exception e) 
		{
			ConsoleHelper.consoleBuildPrintln(e.toString());
			e.printStackTrace();
		}
		
		return m_initPref;
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
				String vcbuildPath    = m_ymlFile.getVcBuildPath() != null ? m_ymlFile.getVcBuildPath() : "";
				String androidSdkPath = m_ymlFile.getAndroidSdkPath() != null ? m_ymlFile.getAndroidSdkPath() : "";
				String androidNdkPath = m_ymlFile.getAndroidNdkPath() != null ? m_ymlFile.getAndroidNdkPath() : "";
				String javaPath       = m_ymlFile.getJavaPath() != null ? m_ymlFile.getJavaPath() : "";
				String bbJdkPath      = m_ymlFile.getBbJdkPath(m_defaultBbVer) != null ? m_ymlFile.getBbJdkPath(m_defaultBbVer) : "";
				String bbMdsPath      = m_ymlFile.getBbMdsPath(m_defaultBbVer) != null ? m_ymlFile.getBbMdsPath(m_defaultBbVer) : "";
				String bbSimPort      = m_ymlFile.getBbSimPort(m_defaultBbVer) != null ? m_ymlFile.getBbSimPort(m_defaultBbVer) : "";
			
				store.setDefault(PreferenceConstants.bbVersionName, m_defaultBbVer);
				store.setDefault(PreferenceConstants.bbJdkPath, bbJdkPath);
				store.setDefault(PreferenceConstants.bbMdsPath, bbMdsPath);
				store.setDefault(PreferenceConstants.bbSim, bbSimPort);
				store.setDefault(PreferenceConstants.javaPath, javaPath);
				store.setDefault(PreferenceConstants.androidSdkParh, androidSdkPath);
				store.setDefault(PreferenceConstants.androidNdkPath, androidNdkPath);
				store.setDefault(PreferenceConstants.cabWizardPath, cabWizPath);
				store.setDefault(PreferenceConstants.vcBuildPath, vcbuildPath);

				store.setValue(PreferenceConstants.bbVersionName, m_defaultBbVer);
				store.setValue(PreferenceConstants.bbJdkPath, bbJdkPath);
				store.setValue(PreferenceConstants.bbMdsPath, bbMdsPath);
				store.setValue(PreferenceConstants.bbSim, bbSimPort);
				store.setValue(PreferenceConstants.javaPath, javaPath);
				store.setValue(PreferenceConstants.androidSdkParh, androidSdkPath);
				store.setValue(PreferenceConstants.androidNdkPath, androidNdkPath);
				store.setValue(PreferenceConstants.cabWizardPath, cabWizPath);
				store.setValue(PreferenceConstants.vcBuildPath, vcbuildPath);
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
				
				String cabWizPath    = store.getString(PreferenceConstants.cabWizardPath);
				String vcbuildPath   = store.getString(PreferenceConstants.vcBuildPath);
				String javaPath      = store.getString(PreferenceConstants.javaPath);
				String sdkPath       = store.getString(PreferenceConstants.androidSdkParh);
				String ndkPath       = store.getString(PreferenceConstants.androidNdkPath);
				String bbVersionName = store.getString(PreferenceConstants.bbVersionName);
				String bbJdkPath     = store.getString(PreferenceConstants.bbJdkPath);
				String bbMdsPath     = store.getString(PreferenceConstants.bbMdsPath);
				String bbSim         = store.getString(PreferenceConstants.bbSim);
				
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

	public void initFromProject(String projectName) 
	{
		try 
		{
			m_currProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			
			if (m_currProject.isOpen())
			{
				AppYmlFile appYmlFile = AppYmlFile.createFromProject(m_currProject);
				m_currRhodesPath  = appYmlFile.getSdkConfigPath();
				
				m_ymlFile = new SdkYmlFile(m_currRhodesPath );
				
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
