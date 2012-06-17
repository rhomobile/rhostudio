package rhogenwizard.rhohub;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.json.JSONArray;
import org.json.JSONException;

import rhogenwizard.constants.ConfigurationConstants;
import rhogenwizard.sdk.task.RubyCodeExecTask;
import rhogenwizard.sdk.task.rhohub.RhoHubAppListTask;
import rhogenwizard.sdk.task.rhohub.RhoHubBuildApp;
import rhogenwizard.sdk.task.rhohub.RhoHubPlatformListTask;
import rhogenwizard.sdk.task.rhohub.RhoHubShowBuildTask;

public class RhoHub implements IRhoHub
{
    private static String rhodesBranchTag = "master";
    
    private static IRhoHub          rhoApi = null;    
    private static IPreferenceStore rhohubConfiguration = null;
    
    public static IRhoHub getInstance(IPreferenceStore configuration)
    {
        if (rhoApi == null)
            rhoApi = new RhoHub();
        
        rhohubConfiguration = configuration;
        
        return rhoApi;
    }
    
    public boolean checkInstallation()
    {
        RubyCodeExecTask task = new RubyCodeExecTask("require 'rhohub'");
        task.run();
        return task.isOk();
    }
        
    private JSONArray getAppList() throws CoreException, JSONException
    {
        if (rhohubConfiguration == null)
            return null;
        
        String rhohubToken   = rhohubConfiguration.getString(ConfigurationConstants.rhoHubToken);
        String rhohubServer  = rhohubConfiguration.getString(ConfigurationConstants.rhoHubUrl);
        
        RhoHubAppListTask task = new RhoHubAppListTask(rhohubToken, rhohubServer);
        task.run();
        
        if (!task.isOk())
            return null;
        
        return task.getOutputAsJSON();
    }
    
    private JSONArray getRemotePlatformList() throws CoreException, JSONException
    {
        if (rhohubConfiguration == null)
            return null;
        
        String rhohubToken   = rhohubConfiguration.getString(ConfigurationConstants.rhoHubToken);
        String rhohubServer  = rhohubConfiguration.getString(ConfigurationConstants.rhoHubUrl);
        
        RhoHubPlatformListTask task = new RhoHubPlatformListTask(rhohubToken, rhohubServer);
        task.run();
        
        if (!task.isOk())
            return null;
        
        return task.getOutputAsJSON();
    }
    
    @Override
    public IRemoteProjectDesc findRemoteApp(IProject project)
    {
        try
        {
            RemoteProjectsList projectList = new RemoteProjectsList (getAppList());

            String localProjectName = project.getName();
            
            for (IRemoteProjectDesc remoteProject : projectList)
            {
                if (localProjectName.equals(remoteProject.getName()))
                {
                    //TODO - hot fix, need add filter parameter to list command 
                    if (remoteProject.getGitLink().contains(localProjectName + "-rhodes"))
                    {
                        RemoteProjectDesc prjDesc = (RemoteProjectDesc) remoteProject;
                        prjDesc.setProject(project);
                        
                        return remoteProject;
                    }
                }
            }
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean buildRemoteApp(IRemoteProjectDesc project)
    {
        String rhohubToken                = rhohubConfiguration.getString(ConfigurationConstants.rhoHubToken);
        String rhohubServer               = rhohubConfiguration.getString(ConfigurationConstants.rhoHubUrl);
        String rhoHubSelectedPlatform     = rhohubConfiguration.getString(ConfigurationConstants.rhoHubSelectedPlatform);
        String rhoHubSelectedRhodesVesion = rhohubConfiguration.getString(ConfigurationConstants.rhoHubSelectedRhodesVesion);

        RhoHubBuildApp task = new RhoHubBuildApp(project, rhohubToken, rhohubServer, rhoHubSelectedPlatform, rhodesBranchTag, rhoHubSelectedRhodesVesion);
        task.run();
        
        try
        {
            RemoteProjectDesc desc = (RemoteProjectDesc) project;
            desc.setBuildInfo(task.getOutputAsJSON());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            System.out.print(task.getError());
        }
        
        return task.isOk();
    }

    @Override
    public boolean pullRemoteAppSources(IRemoteProjectDesc project)
    {
        return false;
    }

    @Override
    public boolean pushSourcesToRemote(IRemoteProjectDesc project)
    {
        return false;
    }

    @Override
    public boolean checkProjectBuildStatus(IRemoteProjectDesc project)
    {
        if (rhohubConfiguration == null)
            return false;
        
        try
        {
            String rhohubToken  = rhohubConfiguration.getString(ConfigurationConstants.rhoHubToken);
            String rhohubServer = rhohubConfiguration.getString(ConfigurationConstants.rhoHubUrl);

            RhoHubShowBuildTask task = new RhoHubShowBuildTask(rhohubToken, rhohubServer, project.getId(), project.getBuildId());
            task.run();
            
            if (!task.isOk())
                return false;
                        
            RemoteProjectDesc prjDesc = (RemoteProjectDesc) project;
            prjDesc.setBuildInfo(task.getOutputAsJSON());
            
            return true;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    public RemotePlatformList getPlatformList()
    {
        try
        {
            return new RemotePlatformList(getRemotePlatformList());
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;       
    }
}
