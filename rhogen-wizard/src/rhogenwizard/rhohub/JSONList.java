package rhogenwizard.rhohub;

import java.util.AbstractCollection;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONList<T extends BaseRemoteDesc> extends AbstractCollection<T> 
{
    private static class ListIterator<I extends BaseRemoteDesc> implements Iterator<I>
    {
        JSONArray              m_descs = null;
        int                    m_index = 0 ;
        JsonAbstractFactory<I> m_factory = null;
        
        ListIterator(JSONArray descs, JsonAbstractFactory<I> objFactory, int index)
        {
            m_descs   = descs;
            m_index   = index;
            m_factory = objFactory;
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
        public I next()
        {
            if (m_descs == null)
                return null;

            try
            {
                int currPos = m_index;
                ++m_index;

                return (I) m_factory.getInstance((JSONObject) m_descs.get(currPos));
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
    
    private JsonAbstractFactory<T> m_objFactory = null;
    private JSONArray              m_buildsDescs = null; 
    
    public JSONList(JSONArray in, JsonAbstractFactory<T> objFactory)
    {
        m_objFactory = objFactory;
        m_buildsDescs = in;
    }
    
    @Override
    public Iterator<T> iterator()
    {
        return new ListIterator<T>(m_buildsDescs, m_objFactory, 0);
    }

    @Override
    public int size()
    {
        if (m_buildsDescs == null)
            return 0;
        
        return m_buildsDescs.length();
    }
}