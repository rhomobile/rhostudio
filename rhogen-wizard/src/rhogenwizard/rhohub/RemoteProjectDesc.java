package rhogenwizard.rhohub;

import org.eclipse.core.resources.IProject;
import org.json.JSONException;
import org.json.JSONObject;

public class RemoteProjectDesc extends BaseRemoteDesc 
{
    public static class RemoteProjectDescFactory implements JsonAbstractFactory<RemoteProjectDesc>
    {
        @Override
        public RemoteProjectDesc getInstance(JSONObject object)
        {
            return new RemoteProjectDesc(object);
        }
    }
    
    private static String idTag           = "id";
    private static String statusTag       = "status";
    private static String gitTag          = "git_repo_url";
    private static String projectNameTag  = "project_name";
    
    private IProject   m_associetedProject = null;
    
    public RemoteProjectDesc(JSONObject object)
    {
        super(object);
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
}
