package rhogenwizard.rhohub;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RemotePlatformDesc
{
    private JSONObject m_baseObject = null;
    private String     m_intName    = null;
    private String     m_publicName = null;
    
    public RemotePlatformDesc(Object object)
    {
        m_baseObject = (JSONObject) object;
        
        JSONArray a = m_baseObject.names();
                
        try
        {
            m_publicName = (String)a.get(0);
            m_intName    = m_baseObject.getString(m_publicName);
        }
        catch (JSONException e)
        {
            m_intName    = null;
            m_publicName = null;
        }
    }
    
    public String getInternalName()
    {
        return m_intName;
    }
    
    public String getPublicName()
    {
        return m_publicName;
    }
}
