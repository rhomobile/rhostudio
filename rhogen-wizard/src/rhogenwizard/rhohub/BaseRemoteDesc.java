package rhogenwizard.rhohub;

import org.json.JSONObject;

public class BaseRemoteDesc
{
    protected JSONObject m_baseObject = null; 
    
    public BaseRemoteDesc(JSONObject baseObject)
    {
        m_baseObject = baseObject;
    }
    
    public JSONObject getJsonObject()
    {
        return m_baseObject;
    }
    
    public void setJsonObject(JSONObject object)
    {
        m_baseObject = object;
    }
}
