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
import rhogenwizard.RunExeHelper;
import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.buildfile.SdkYmlFile;
import rhogenwizard.rhohub.IRhoHubSetting;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
    static String                rhodesDefaultVersion = "3.3.2";
    static PreferenceInitializer initPref             = null;

    private SdkYmlFile           m_ymlFile            = null;

    public static PreferenceInitializer getInstance()
    {
        try
        {
            if (initPref == null)
            {
                initPref = new PreferenceInitializer();
            }
            File rhobuildYml = findRhobuildYml();
            try
            {
                initPref.m_ymlFile = new SdkYmlFile(rhobuildYml.getAbsolutePath());
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            initPref.initializeDefaultPreferences();
        }
        catch (Exception e)
        {
            Activator.logError(e);
        }

        return initPref;
    }

    public static String getRhodesPath()
    {
        return findRhobuildYml().getParentFile().toString();
    }

    public void initializeDefaultPreferences()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        try
        {
            if (m_ymlFile != null)
            {
                String cabWizPath = m_ymlFile.getCabWizPath() != null ? m_ymlFile.getCabWizPath() : "";
                String vcbuildPath = m_ymlFile.getVcBuildPath() != null ? m_ymlFile.getVcBuildPath() : "";
                String androidSdkPath = m_ymlFile.getAndroidSdkPath() != null ? m_ymlFile.getAndroidSdkPath() : "";
                String androidNdkPath = m_ymlFile.getAndroidNdkPath() != null ? m_ymlFile.getAndroidNdkPath() : "";
                String javaPath = m_ymlFile.getJavaPath() != null ? m_ymlFile.getJavaPath() : "";

                store.setDefault(PreferenceConstants.javaPath, javaPath);
                store.setDefault(PreferenceConstants.androidSdkParh, androidSdkPath);
                store.setDefault(PreferenceConstants.androidNdkPath, androidNdkPath);
                store.setDefault(PreferenceConstants.cabWizardPath, cabWizPath);
                store.setDefault(PreferenceConstants.vcBuildPath, vcbuildPath);

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

                String cabWizPath = store.getString(PreferenceConstants.cabWizardPath);
                String vcbuildPath = store.getString(PreferenceConstants.vcBuildPath);
                String javaPath = store.getString(PreferenceConstants.javaPath);
                String sdkPath = store.getString(PreferenceConstants.androidSdkParh);
                String ndkPath = store.getString(PreferenceConstants.androidNdkPath);

                m_ymlFile.setJavaPath(javaPath);
                m_ymlFile.setCabWizPath(cabWizPath);
                m_ymlFile.setVcBuildPath(vcbuildPath);
                m_ymlFile.setAndroidNdkPath(ndkPath);
                m_ymlFile.setAndroidSdkPath(sdkPath);

                m_ymlFile.save();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static List<String> getRhodesProjects()
    {
        List<String> namesList = new ArrayList<String>();

        for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects())
        {
            if (p.isOpen() && buildYml(p).exists())
            {
                namesList.add(p.getName());
            }
        }

        return namesList;
    }

    private static File buildYml(IProject project)
    {
        return new File(project.getLocation() + File.separator + AppYmlFile.configFileName);
    }

    private static File findRhobuildYml()
    {
        IPath rhobuildYml = null;

        List<String> projectNames = getRhodesProjects();

        for (String projectName : projectNames)
        {
            IProject currProject =
                ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

            if (AppYmlFile.isExists(currProject.getLocation().toOSString()))
            {
                try
                {
                    if (currProject.isOpen())
                    {
                        AppYmlFile appYmlFile = AppYmlFile.createFromProject(currProject);
                        rhobuildYml = new Path(appYmlFile.getSdkConfigPath());

                        if (!rhobuildYml.isAbsolute())
                        {
                            IPath basePath = new Path(currProject.getLocation().toOSString());
                            rhobuildYml = basePath.append(rhobuildYml);
                        }
                    }
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                break;
            }
        }

        if (rhobuildYml == null)
        {
            rhobuildYml =
                new Path(RunExeHelper.getSdkInfo() + File.separator + SdkYmlFile.configName);
        }
        return rhobuildYml.toFile();
    }
}
