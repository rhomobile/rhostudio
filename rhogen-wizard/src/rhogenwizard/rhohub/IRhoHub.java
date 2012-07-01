package rhogenwizard.rhohub;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.json.JSONException;

public interface IRhoHub
{
    //
    IRemoteProjectDesc findRemoteApp(IProject project);
    //
    RemotePlatformList getPlatformList();
    //
    RemoteProjectsList getProjectsList() throws CoreException, JSONException, InterruptedException;
    //
    boolean buildRemoteApp(IRemoteProjectDesc project);
    //
    boolean pullRemoteAppSources(IRemoteProjectDesc project, final CredentialsProvider credProvider)  throws InvalidRemoteException;
    //
    boolean pushSourcesToRemote(IRemoteProjectDesc project, final CredentialsProvider credProvider) throws InvalidRemoteException;
    //
    boolean checkProjectBuildStatus(IRemoteProjectDesc project);
    //
    boolean isRemoteProjectExist(IProject project);
    //
    IRemoteProjectDesc createRemoteAppFromLocalSources(IProject project, final CredentialsProvider credProvider) throws InvalidRemoteException;
    //
    IRemoteProjectDesc updateRemoteAppFromLocalSources(IProject project, String gitRepo, final CredentialsProvider credProvider) throws InvalidRemoteException;
    //
    RemoteAppBuildsList getBuildsList(IProject project) throws JSONException;
}
