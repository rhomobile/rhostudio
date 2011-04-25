package rhogenwizard.buildfile;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CustomConverter extends AbstractStructureConverter 
{

	@Override
	public String convertStructure() 
	{
		StringBuilder sb = new StringBuilder();
		
	    Iterator it = m_dataStructure.entrySet().iterator();
	    
	    while (it.hasNext()) 
	    {
	        Map.Entry pairs = (Map.Entry)it.next();
	        
	        Object key = (Object) pairs.getKey();
	        Object val = pairs.getValue();
	        
	        saveSelector(sb, "", key.toString(), val);
	    }
	    
	    return sb.toString();
	}
	
	void saveSelector(StringBuilder sb, String prefix, String name, Object val)
	{
        if (val instanceof List)
        {
        	saveList(sb, prefix, name, (List)val);
        }
        else if (val instanceof Map)
        {
        	saveMap(sb, prefix, name, (Map)val);
        }
        else
        {
        	saveValue(sb, prefix, name, val);
        }
	}
	
	private void saveValue(StringBuilder sb, String prefix, String name, Object l)
	{
		sb.append(prefix);
		sb.append(name);
		sb.append(": ");
		
		if (l != null) 
		{
			if (l instanceof String)
			{
				String itemValue = l.toString();
				itemValue = itemValue.replace("\\", "/");

				char firstChar = itemValue.charAt(0);
				char lstChar   = itemValue.charAt(itemValue.length() - 1);
				
				if (firstChar == '*' || lstChar == '*')
				{
					sb.append("\"");
					sb.append(itemValue.toString());
					sb.append("\"");
				}
				else
				{
					sb.append(itemValue.toString());
				}
			}
			else
			{
				sb.append(l.toString());
			}
		}
		
		sb.append("\n");
	}
	
	private void saveList(StringBuilder sb, String prefix, String name, List l)
	{
		sb.append(prefix);
		sb.append(name);
		sb.append(": \n");
		
		for (int i=0; i<l.size(); ++i)
		{
			Object val = l.get(i);

			sb.append(prefix);
			sb.append("  - ");
			
			String renderVal = val.toString();
			
			char firstChar = renderVal.charAt(0);
			char lstChar   = renderVal.charAt(renderVal.length() - 1);
			
			if (firstChar == '*' || lstChar == '*')
			{
				sb.append("\"");
				sb.append(val.toString());
				sb.append("\"");
			}
			else
			{
				sb.append(val.toString());
			}
			
			sb.append("\n");		
		}
	}
	
	private void saveMap(StringBuilder sb, String prefix, String name,  Map m)
	{
		if (name != null)
		{
			sb.append(prefix);
			sb.append(name);
			sb.append(":\n");
		}
		
	    Iterator it = m.entrySet().iterator();
	    
	    while (it.hasNext()) 
	    {
	        Map.Entry pairs = (Map.Entry)it.next();
	        
	        Object key = (Object) pairs.getKey();
	        Object val = pairs.getValue();
	        
	        saveSelector(sb, prefix + "  ", key.toString(), val);
	    }
	}
}
