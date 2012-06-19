package rhogenwizard.rhohub;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.json.JSONException;
import org.json.JSONObject;

public class RemoteProjectDesc implements IRemoteProjectDesc
{
    private static String idTag           = "id";
    private static String statusTag       = "status";
    private static String gitTag          = "git_repo_url";
    private static String projectNameTag  = "project_name";
    private static String downloadLinkTag = "download_link";
    
    private JSONObject m_baseObject        = null;
    private JSONObject m_buildInfoObject   = null;
    private IProject   m_associetedProject = null;
    
    public RemoteProjectDesc(Object object)
    {
        m_baseObject = (JSONObject) object;
    }
    
    public Integer getId() throws JSONException
    {
        return (Integer)m_baseObject.get(idTag);
    }
    
    public String getGitLink() throws JSONException
    {
        return (String)m_baseObject.get(gitTag);
    }
    
    public RemoteStatus getStatus() throws JSONException
    {
        return RemoteStatus.fromString((String)m_baseObject.get(statusTag));
    }
    
    public String getName() throws JSONException
    {
        return (String)m_baseObject.get(projectNameTag);
    }
    
    public IProject getProject()
    {
        return m_associetedProject;
    }
    
    public void setProject(IProject associetedProject)
    {
        m_associetedProject = associetedProject;
    }
    
    public void setId(Integer newId) throws JSONException
    {
        m_baseObject.put(idTag, newId);
    }
    
    public void setStatus(JSONObject newStatus) throws JSONException
    {
        m_baseObject.put(statusTag, (String)newStatus.get(statusTag));
    }

    @Override
    public Integer getBuildId() throws JSONException
    {
        if (m_buildInfoObject == null)
            return null;
        
        return (Integer)m_buildInfoObject.get(idTag);
    }
    
    public void setBuildInfo(JSONObject buildInfo)
    {
        m_buildInfoObject = buildInfo;
    }

    @Override
    public RemoteStatus getBuildStatus() throws JSONException
    {
        if (m_buildInfoObject == null)
            return null;
        
        return RemoteStatus.fromString((String)m_buildInfoObject.get(statusTag));
    }

    @Override
    public URL getBuildResultUrl() throws JSONException, MalformedURLException
    {
        if (m_buildInfoObject == null)
            return null;
        
        return new URL((String)m_buildInfoObject.get(downloadLinkTag));
    }

    @Override
    public String getBuildResultFileName() throws JSONException
    {
        String dwlLink = (String)m_buildInfoObject.get(downloadLinkTag);
        
        int nameStartIdx = dwlLink.lastIndexOf("/");
        
        if (nameStartIdx != -1)
        {
            return dwlLink.substring(nameStartIdx);    
        }
        
        return null;
    }
}
