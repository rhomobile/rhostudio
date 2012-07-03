package rhogenwizard.rhohub;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class RemoteAppBuildDesc extends BaseRemoteDesc
{
    public static class RemoteAppBuildDescFactory implements JsonAbstractFactory<RemoteAppBuildDesc>
    {
        @Override
        public RemoteAppBuildDesc getInstance(JSONObject object)
        {
            return new RemoteAppBuildDesc(object);
        }
    }

    private static String idTag           = "id";
    private static String statusTag       = "status";
    private static String downloadLinkTag = "download_link";
    
    public RemoteAppBuildDesc(JSONObject object)
    {
        super(object);
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
    
    public String getBuildResultFileName() throws JSONException
    {
        String dwlLink = (String)m_baseObject.get(downloadLinkTag);
          
        int nameStartIdx = dwlLink.lastIndexOf("/");
          
        if (nameStartIdx != -1)
        {
            return dwlLink.substring(nameStartIdx);    
        }
          
        return null;
    }
}
