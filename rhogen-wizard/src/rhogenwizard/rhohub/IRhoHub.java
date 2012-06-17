package rhogenwizard.rhohub;

import org.eclipse.core.resources.IProject;

public interface IRhoHub
{
    //
    IRemoteProjectDesc findRemoteApp(IProject project);
    //
    RemotePlatformList getPlatformList();
    //
    boolean buildRemoteApp(IRemoteProjectDesc project);
    //
    boolean pullRemoteAppSources(IRemoteProjectDesc project);
    //
    boolean pushSourcesToRemote(IRemoteProjectDesc project);
    //
    boolean checkProjectBuildStatus(IRemoteProjectDesc project);
}
