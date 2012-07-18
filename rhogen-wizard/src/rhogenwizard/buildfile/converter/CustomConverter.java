package rhogenwizard.buildfile.converter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.yaml.snakeyaml.Yaml;


public class CustomConverter extends AbstractStructureConverter 
{
	private static final String shiftLevel          = "  "; 
	private static final String crCode              = "\n";
	private static final char   rubyCommentsCode    = '#';
	private static final String rubyCommentsCodeStr = "#";
	
	Map<Integer, String> m_commentsStorage = new HashMap<Integer, String>();   
	
	@Override
	public String convertStructure() 
	{
		StringBuilder sb = new StringBuilder();
		
	    Iterator<Map.Entry<Object, Object>> it = m_dataStructure.entrySet().iterator();
	    
	    while (it.hasNext()) 
	    {
	        Map.Entry<Object, Object> pairs = it.next();
	        
	        Object key = pairs.getKey();
	        Object val = pairs.getValue();
	        
	        if (key != null)
	        {
	        	saveSelector(sb, "", key.toString(), val);
	        }
	    }
	    
	    return addComments(sb.toString());
	}
	
	private String addComments(String rawYmlData)
	{
	    StringTokenizer st = new StringTokenizer(rawYmlData, crCode);
	    StringBuilder   sb = new StringBuilder();
	    
	    int lineNum = 0;
	    while (st.hasMoreTokens())
	    {
	    	String tokenString = st.nextToken();
	    	
	    	String comments = m_commentsStorage.get(new Integer(lineNum)); 
	    		
	    	if (comments != null)
	    	{
	    		sb.append(comments);
	    	}
	    	
	    	sb.append(tokenString);
	    	sb.append(crCode);
	    	
	    	lineNum++;
	    }

	    return sb.toString();
	}
	
	void saveSelector(StringBuilder sb, String prefix, String name, Object val)
	{
		if (name == null || name.isEmpty())
			return;
		
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

				if (itemValue.length() != 0)
				{
					sb.append("\"");
					sb.append(itemValue.toString());
					sb.append("\"");
				}
			}
			else
			{
				sb.append(l.toString());
			}
		}
		
		sb.append(crCode);
	}
	
	private void saveList(StringBuilder sb, String prefix, String name, List<Object> l)
	{
		sb.append(prefix);
		sb.append(name);
		sb.append(":");
		sb.append(crCode);
		
		for (int i=0; i<l.size(); ++i)
		{
			Object val = l.get(i);

			sb.append(prefix);
			sb.append(shiftLevel + "- ");
			
			String renderVal = val.toString();
			
			if (renderVal.length() != 0)
			{
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
			}
			
			sb.append(crCode);		
		}
	}
	
	private void saveMap(StringBuilder sb, String prefix, String name,  Map<Object, Object> m)
	{
		if (name != null)
		{
			sb.append(prefix);
			sb.append(name);
			sb.append(":");
			sb.append(crCode);
		}
		
	    Iterator it = m.entrySet().iterator();
	    
	    while (it.hasNext()) 
	    {
	        Map.Entry pairs = (Map.Entry)it.next();
	        
	        Object key = (Object) pairs.getKey();
	        Object val = pairs.getValue();
	        
	        saveSelector(sb, prefix + shiftLevel, key.toString(), val);
	    }
	}

	private void fillCommentsMap(String filePath)
	{
		try
		{
		  	FileInputStream fStream = new FileInputStream(filePath);
		  	DataInputStream in      = new DataInputStream(fStream);
		  	BufferedReader  br      = new BufferedReader(new InputStreamReader(in));
		  	String          strLine = null;
		  	StringBuilder   commentsBuilder = new StringBuilder(); 
		  	
		  	int lineNum = 0;
		  	while((strLine = br.readLine()) != null)
		  	{
		  		String trimLine = strLine.trim();
		  		
		  		if (trimLine.length() != 0)
		  		{
			  		if (trimLine.charAt(0) == rubyCommentsCode)
			  		{
			  			commentsBuilder.append(trimLine);
			  			commentsBuilder.append(crCode);
			  			continue;
			  		}
			  		else
			  		{
			  			if (trimLine.contains(rubyCommentsCodeStr))
			  			{
			  				int startChar = trimLine.indexOf(rubyCommentsCodeStr);
			  				
			  				if (startChar > 0) 
			  				{
			  					m_commentsStorage.put(new Integer(lineNum), trimLine.substring(startChar, trimLine.length() - 1) + crCode);
			  					continue;
			  				}
			  			}
			  		}
		  		}
		  		
	  			if (commentsBuilder.length() != 0)
	  			{	
	  				m_commentsStorage.put(new Integer(lineNum), commentsBuilder.toString());
	  				commentsBuilder = new StringBuilder();
	  			}
	  			
	  			++lineNum;
		  	}
		  	
		  	br.close();
		  	in.close();
		  	fStream.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
    @Override
	public Map<Object, Object> getDataStorage(String filePath) throws FileNotFoundException
	{
		File       ymlFile = new File(filePath);
		Yaml       yaml    = new Yaml();		
		FileReader fr      = new FileReader(ymlFile);

		fillCommentsMap(filePath);
		
		return cast(yaml.load(fr));
	}

	@SuppressWarnings("unchecked")
	private Map<Object, Object> cast(Object mapObject)
	{
        return (Map<Object, Object>) mapObject;
	}
}
