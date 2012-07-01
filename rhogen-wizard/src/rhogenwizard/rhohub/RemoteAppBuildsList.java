package rhogenwizard.rhohub;

import java.util.AbstractCollection;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;

public class RemoteAppBuildsList extends AbstractCollection<RemoteAppBuildDesc>
{
    private static class ProjectBuidlsListIterator implements Iterator<RemoteAppBuildDesc>
    {
        JSONArray m_descs = null;
        int       m_index = 0 ;
                
        ProjectBuidlsListIterator(JSONArray descs, int index)
        {
            m_descs = descs;
            m_index = index;
        }
        
        @Override
        public boolean hasNext()
        {
            if (m_descs == null)
                return false;
             
            try
            {
                return m_descs.get(m_index) == null ? false : true;
            }
            catch (JSONException e)
            {
                return false;
            }
        }

        @Override
        public RemoteAppBuildDesc next()
        {
            if (m_descs == null)
                return null;

            try
            {
                int currPos = m_index;
                ++m_index;

                return new RemoteAppBuildDesc(m_descs.get(currPos));
            }
            catch (JSONException e)
            {
            }
            
            return null;
        }

        @Override
        public void remove()
        {
            if (m_descs != null)
            {
                m_descs.remove(m_index);
            }
        }
    }
    
    private JSONArray m_buildsDescs = null; 
    
    public RemoteAppBuildsList(JSONArray in)
    {
        m_buildsDescs = in;
    }
    
    @Override
    public Iterator<RemoteAppBuildDesc> iterator()
    {
        return new ProjectBuidlsListIterator(m_buildsDescs, 0);
    }

    @Override
    public int size()
    {
        if (m_buildsDescs == null)
            return 0;
        
        return m_buildsDescs.length();
    }
}