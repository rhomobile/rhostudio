package rhogenwizard.rhohub;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class RemoteAppBuildDesc
{
    private static String idTag           = "id";
    private static String statusTag       = "status";
    private static String downloadLinkTag = "download_link";
    
    private JSONObject m_baseObject        = null;
    
    public RemoteAppBuildDesc(Object object)
    {
        m_baseObject = (JSONObject) object;
    }
    
    public RemoteStatus getStatus() throws JSONException
    {
        return RemoteStatus.fromString((String)m_baseObject.get(statusTag));
    }
    
    public Integer getId() throws JSONException
    {
        return (Integer)m_baseObject.get(idTag);
    }
    
    public RemoteStatus getBuildStatus() throws JSONException
    {
        return RemoteStatus.fromString((String)m_baseObject.get(statusTag));
    }

    public URL getBuildResultUrl() throws JSONException, MalformedURLException
    {
        return new URL((String)m_baseObject.get(downloadLinkTag));
    }
}
