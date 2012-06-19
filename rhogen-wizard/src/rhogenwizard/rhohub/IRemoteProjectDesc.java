package rhogenwizard.rhohub;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.json.JSONException;

public interface IRemoteProjectDesc
{
    public Integer getId() throws JSONException;
    //
    public String getGitLink() throws JSONException;
    //
    public RemoteStatus getStatus() throws JSONException;
    //
    public String getName() throws JSONException;
    //
    public IProject getProject();
    //
    public Integer getBuildId() throws JSONException;
    //
    public RemoteStatus getBuildStatus() throws JSONException;
    //
    public URL getBuildResultUrl() throws JSONException, MalformedURLException;
    //
    public String getBuildResultFileName() throws JSONException;
}