package rhogenwizard.rhohub;

import java.util.AbstractCollection;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;

public class RemotePlatformList extends AbstractCollection<RemotePlatformDesc>
{
    private static class ProjectsListIterator implements Iterator<RemotePlatformDesc>
    {
        JSONArray m_descs = null;
        int       m_index = 0 ;
                
        ProjectsListIterator(JSONArray descs, int index)
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
        public RemotePlatformDesc next()
        {
            if (m_descs == null)
                return null;

            try
            {
                int currPos = m_index;
                ++m_index;

                return new RemotePlatformDesc(m_descs.get(currPos));
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
    
    private JSONArray m_projectDescs = null; 
    
    public RemotePlatformList(JSONArray in)
    {
        m_projectDescs = in;
    }
    
    @Override
    public Iterator<RemotePlatformDesc> iterator()
    {
        return new ProjectsListIterator(m_projectDescs, 0);
    }

    @Override
    public int size()
    {
        if (m_projectDescs == null)
            return 0;
        
        return m_projectDescs.length();
    }
}