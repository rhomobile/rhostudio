package rhogenwizard.rhohub;

import org.json.JSONException;
import org.json.JSONObject;

public class NewRemoteProjectDesc
{
    private static String rhodesId      = "rhodes_id";
    private static String rhodesGitLink = "rhodes_repo_url";
    private static String rhodesName    = "project_name";
    
    private JSONObject m_projectInfo = null;
    
    public NewRemoteProjectDesc(JSONObject prjInfo)
    {
        m_projectInfo = prjInfo;
    }
    
    public String getGitRepo() throws JSONException
    {
        return m_projectInfo.getString(rhodesGitLink);
    }
        
    public Integer getAppId() throws JSONException
    {
        return new Integer(m_projectInfo.getInt(rhodesId));
    }
    
    public String getName() throws JSONException
    {
        return m_projectInfo.getString(rhodesName);
    }
}
