package rhogenwizard.rhohub;

import java.io.File;
import java.io.IOException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.json.JSONArray;
import org.json.JSONException;

import rhogenwizard.sdk.task.RubyCodeExecTask;
import rhogenwizard.sdk.task.rhohub.AppListTask;
import rhogenwizard.sdk.task.rhohub.BuildApp;
import rhogenwizard.sdk.task.rhohub.PlatformListTask;
import rhogenwizard.sdk.task.rhohub.ShowBuildTask;

public class RhoHub implements IRhoHub
{
    private static IRhoHub          rhoApi = null;    
    private static IRhoHubSetting   rhohubConfiguration = null;
    
    public static IRhoHub getInstance(IRhoHubSetting configuration)
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
        
        AppListTask task = new AppListTask(rhohubConfiguration);
        task.run();
        
        if (!task.isOk())
            return null;
        
        return task.getOutputAsJSON();
    }
    
    private JSONArray getRemotePlatformList() throws CoreException, JSONException
    {
        if (rhohubConfiguration == null)
            return null;
                
        PlatformListTask task = new PlatformListTask(rhohubConfiguration);
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
            
            Git localRepo = Git.open(new File(project.getLocation().toOSString()));
            StoredConfig repoConfig = localRepo.getRepository().getConfig();
            
            String localRepoUrl = repoConfig.getString("remote", "origin", "url");
            
            for (IRemoteProjectDesc remoteProject : projectList)
            {
                if (remoteProject.getGitLink().equals(localRepoUrl))
                {
                    RemoteProjectDesc prjDesc = (RemoteProjectDesc) remoteProject;
                    prjDesc.setProject(project);
                    
                    return remoteProject;
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
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean buildRemoteApp(IRemoteProjectDesc project)
    {
        BuildApp task = new BuildApp(project, rhohubConfiguration);
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
    
    public void initGitRepo(IProject project)
    {
    }
    
    private void replaceRemoteSourcesFromLocal(IProject project, final String userPwd, final String remoteGitRepo)
    {
        InitCommand initCmd = Git.init();
        
        File repoDir = new File(project.getLocation().toOSString());
        
        initCmd.setDirectory(repoDir);
        initCmd.call();

        try
        {
            Git localRepo = Git.open(repoDir);

            AddCommand addCmd = localRepo.add();
            addCmd.addFilepattern(".");
            addCmd.call();
            
            CommitCommand commitCmd = localRepo.commit();
            commitCmd.setAll(true);
            commitCmd.setMessage("replace remote sources from local computer");
            commitCmd.call();
            
            StoredConfig repoCfg = localRepo.getRepository().getConfig();
            repoCfg.setString("remote", "origin", "url", remoteGitRepo);
            repoCfg.setString("remote", "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*");
            repoCfg.save();
            
            PushCommand pushCmd = localRepo.push();
            pushCmd.setForce(true);
            pushCmd.setCredentialsProvider(new CredentialsProvider()
            {
                @Override
                public boolean supports(CredentialItem... arg0) {
                    return false;
                }
                
                @Override
                public boolean isInteractive() {
                    return false;
                }
                
                @Override
                public boolean get(URIish arg0, CredentialItem... arg1) throws UnsupportedCredentialItem
                {
                    CredentialItem.StringType pwdCred = (CredentialItem.StringType )arg1[0];
                    pwdCred.setValue(userPwd);
                    return true;
                }
            });
            pushCmd.call();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (NoFilepatternException e)
        {
            e.printStackTrace();
        }
        catch (NoHeadException e)
        {
            e.printStackTrace();
        }
        catch (NoMessageException e)
        {
            e.printStackTrace();
        }
        catch (ConcurrentRefUpdateException e)
        {
            e.printStackTrace();
        }
        catch (JGitInternalException e)
        {
            e.printStackTrace();
        }
        catch (WrongRepositoryStateException e)
        {
            e.printStackTrace();
        }
        catch (InvalidRemoteException e)
        {
            e.printStackTrace();
        }
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
            ShowBuildTask task = new ShowBuildTask(rhohubConfiguration, project.getId(), project.getBuildId());
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

    @Override
    public boolean isRemoteProjectExist(IProject project)
    {
        return findRemoteApp(project) == null ? false : true;
    }
}
