package rhogenwizard.rhohub;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.json.JSONException;

public interface IRhoHub
{
    //
    RemoteProjectDesc findRemoteApp(IProject project);
    //
    JSONList<RemotePlatformDesc> getPlatformList();
    //
    JSONList<RemoteProjectDesc> getProjectsList() throws CoreException, JSONException, InterruptedException;
    //
    RemoteAppBuildDesc buildRemoteApp(RemoteProjectDesc project);
    //
    boolean pushSourcesToRemote(RemoteProjectDesc project, final CredentialsProvider credProvider) throws InvalidRemoteException;
    //
    boolean checkProjectBuildStatus(RemoteProjectDesc projectInfo, RemoteAppBuildDesc buildInfo);
    //
    boolean isRemoteProjectExist(IProject project);
    //
    RemoteProjectDesc createRemoteAppFromLocalSources(IProject project, final CredentialsProvider credProvider) throws InvalidRemoteException;
    //
    RemoteProjectDesc updateRemoteAppFromLocalSources(IProject project, String gitRepo, final CredentialsProvider credProvider) throws InvalidRemoteException;
    //
    JSONList<RemoteAppBuildDesc> getBuildsList(IProject project) throws JSONException;
}
