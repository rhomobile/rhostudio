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
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.json.JSONArray;
import org.json.JSONException;

import rhogenwizard.sdk.task.RubyCodeExecTask;
import rhogenwizard.sdk.task.rhohub.AppListTask;
import rhogenwizard.sdk.task.rhohub.BuildApp;
import rhogenwizard.sdk.task.rhohub.BuildListTask;
import rhogenwizard.sdk.task.rhohub.CreateAppTask;
import rhogenwizard.sdk.task.rhohub.PlatformListTask;
import rhogenwizard.sdk.task.rhohub.ShowBuildTask;

public class RhoHub implements IRhoHub
{
    private static IRhoHub        rhoApi = null;    
    private static IRhoHubSetting rhohubConfiguration = null;
    
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
        
    private JSONArray getAppList() throws CoreException, JSONException, InterruptedException
    {
        if (rhohubConfiguration == null)
            return null;
        
        AppListTask task = new AppListTask(rhohubConfiguration);
        task.run();
        
        String s  = task.getError();
        
        if (!task.isOk())
            return null;
        
        return task.getOutputAsJSON();
    }
    
    private JSONArray getRemotePlatformList() throws CoreException, JSONException, InterruptedException
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
    public RemoteProjectDesc findRemoteApp(IProject project)
    {
        try
        {
            JSONList<RemoteProjectDesc> projectList = new JSONList<RemoteProjectDesc> (getAppList(), new RemoteProjectDesc.RemoteProjectDescFactory());
            
            Git localRepo = Git.open(new File(project.getLocation().toOSString()));
            StoredConfig repoConfig = localRepo.getRepository().getConfig();
            
            String localRepoUrl = repoConfig.getString("remote", "origin", "url");
            
            for (RemoteProjectDesc remoteProject : projectList)
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
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public RemoteAppBuildDesc buildRemoteApp(RemoteProjectDesc project)
    {
        BuildApp task = new BuildApp(project, rhohubConfiguration);
        task.run();
        
        try
        {
            if (!task.isOk())
                return null;
            
            return new RemoteAppBuildDesc(task.getOutputAsJSON());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            System.out.print(task.getError());
        }
        
        return null;
    }
    
    private boolean replaceRemoteSourcesFromLocal(IProject project, final String remoteGitRepo, final CredentialsProvider credProvider) throws InvalidRemoteException
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
            pushCmd.setCredentialsProvider(credProvider);
            pushCmd.call();
            
            return true;
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
        
        return false;
    }
    
    @Override
    public boolean pushSourcesToRemote(RemoteProjectDesc project, final CredentialsProvider credProvider) throws InvalidRemoteException
    {
        try
        {
            Git localRepo = Git.open((File) project.getProject().getLocation());
            
            PushCommand pushCmd = localRepo.push();
            pushCmd.setForce(true);
            pushCmd.setCredentialsProvider(credProvider);
            pushCmd.call();
            
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (JGitInternalException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean checkProjectBuildStatus(RemoteProjectDesc projectInfo, RemoteAppBuildDesc buildInfo)
    {
        if (rhohubConfiguration == null)
            return false;
        
        try
        {
            ShowBuildTask task = new ShowBuildTask(rhohubConfiguration, projectInfo.getId(), buildInfo.getId());
            task.run();
            
            if (!task.isOk())
                return false;
                        
            buildInfo.setJsonObject(task.getOutputAsJSON());
            
            return true;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    public JSONList<RemotePlatformDesc>  getPlatformList()
    {
        try
        {
            return new JSONList<RemotePlatformDesc>(getRemotePlatformList(), new RemotePlatformDesc.RemotePlatformDescFactory());
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
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

    @Override
    public JSONList<RemoteProjectDesc> getProjectsList() throws CoreException, JSONException, InterruptedException
    {
        return new JSONList<RemoteProjectDesc> (getAppList(), new RemoteProjectDesc.RemoteProjectDescFactory());
    }

    private NewRemoteProjectDesc createRemoteApp(IProject project) throws JSONException
    {
        CreateAppTask task = new CreateAppTask(rhohubConfiguration, project.getName());
        task.run();
        
        return new NewRemoteProjectDesc(task.getOutputAsJSON());
    }
    
    @Override
    public RemoteProjectDesc createRemoteAppFromLocalSources(IProject project, final CredentialsProvider credProvider) throws InvalidRemoteException
    {
        try
        {
            NewRemoteProjectDesc newProjectDesc = createRemoteApp(project);
            
            return updateRemoteAppFromLocalSources(project, newProjectDesc.getGitRepo(), credProvider);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return null;
    }

    @Override
    public RemoteProjectDesc updateRemoteAppFromLocalSources(IProject project, String gitRepoUrl, CredentialsProvider credProvider) throws InvalidRemoteException
    {
        if (replaceRemoteSourcesFromLocal(project, gitRepoUrl, credProvider))
        {
            return findRemoteApp(project);
        }
        
        return null;
    }

    @Override
    public JSONList<RemoteAppBuildDesc> getBuildsList(IProject project) throws JSONException
    {
        RemoteProjectDesc remoteProject = findRemoteApp(project);

        BuildListTask task = new BuildListTask(rhohubConfiguration, remoteProject.getId());
        task.run();
        
        return new JSONList<RemoteAppBuildDesc>(task.getOutputAsJSON(), new RemoteAppBuildDesc.RemoteAppBuildDescFactory());
    }
}
