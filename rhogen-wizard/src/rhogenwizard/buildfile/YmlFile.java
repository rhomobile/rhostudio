package rhogenwizard.buildfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import rhogenwizard.ConsoleHelper;

public class YmlFile
{
	private static boolean SNAKE_YAML_SAVE = false;
	
	private String m_filePath = null;
	private Map m_dataStorage = null;
	
	public YmlFile()
	{}
	
	public YmlFile(String ymlFileName) throws FileNotFoundException
	{
		m_filePath = ymlFileName;
		File ymlFile = new File(ymlFileName);
		
		if (ymlFile.exists()) {
			load(ymlFile);
		}
	}
	
	public YmlFile(File ymlFile) throws FileNotFoundException
	{
		m_filePath = ymlFile.getAbsolutePath();
		load(ymlFile);
	}
	
	public String getPath()
	{
		return m_filePath;
	}
	
	private void load(File ymlFile) throws FileNotFoundException
	{
		final Yaml yaml = new Yaml();
		FileReader fr = new FileReader(ymlFile);
		
		m_dataStorage = (Map) yaml.load(fr);
	}
	
	public void fromString(String ymlFiledata)
	{
		final Yaml yaml = new Yaml();
		m_dataStorage = (Map) yaml.load(ymlFiledata);
	}
	
	public String get(String sectionName, String paramName)
	{
		Map section = (Map) m_dataStorage.get(sectionName);
		
		if (null != section)
		{
			return (String) section.get(paramName);
		}
		
		return null;
	}
	
	public String get(String mainSection, String sectionName, String paramName)
	{
		Map mSection = (Map) m_dataStorage.get(mainSection);
		
		if (null != mSection)
		{
			Map section = (Map) mSection.get(sectionName);
			
			if (null != section)
			{
				return (String) section.get(paramName);
			}
		}
		
		return null;
	}
	

	public Object getObject(String mainSection, String sectionName)
	{
		Map mSection = (Map) m_dataStorage.get(mainSection);
		
		if (null != mSection)
		{
			return  mSection.get(sectionName);
		}
		
		return null;
	}
	
	public String get(String commonSection, String mainSection, String sectionName, String paramName)
	{
		try
		{
			Map comSection =  (Map) m_dataStorage.get(commonSection);
			
			if (comSection == null)
				return null;
			
			Map mSection = (Map) comSection.get(mainSection);
			
			if (null != mSection)
			{
				Map section = (Map) mSection.get(sectionName);
				
				if (null != section)
				{
					return (String) section.get(paramName).toString();
				}
				
				section = (Map) mSection.get(new Double(sectionName));
				
				if (null != section)
				{
					return (String) section.get(paramName).toString();
				}
			}
		}
		catch(Exception e) {
		}

		return null;
	}
	
	public void set(String commonSection, String mainSection, Object sectionName, String paramName, Object value)
	{
		try
		{
			Map comSection =  (Map) m_dataStorage.get(commonSection);
			
			if (comSection != null)
			{			
				Map mSection = (Map) comSection.get(mainSection);
				
				if (null != mSection)
				{
					Map section = (Map) mSection.get(sectionName);
					
					if (null != section)
					{
						section.put(paramName, value);
					}
					else
					{
						LinkedHashMap m = new LinkedHashMap();
						m.put(paramName, value);
						mSection.put(sectionName, m);
					}
				}
			}
		}
		catch(Exception e) 
		{
		}
	}
	
	public String getString(String sectionName)
	{
		String section = (String) m_dataStorage.get(sectionName);
		return section;
	}
	
	public Object getObject(String sectionName)
	{
		Object section = (Object) m_dataStorage.get(sectionName);
		return section;
	}

	public void set(String mainSection, String subSection, String paramName, Object value) 
	{
		Map mSection = (Map) m_dataStorage.get(mainSection);
		
		if (null != mSection)
		{
			Map section = (Map) mSection.get(subSection);
			
			if (null != section)
			{
				section.put(paramName, value);
			}
		}
	}

	public void set(String sectionName, Object value) 
	{
		m_dataStorage.put(sectionName, value);
	}
	
	public void save() throws FileNotFoundException
	{
		try 
		{
			if (m_filePath.length() != 0)
			{
				String dataString = null;

				File outFile = new File(m_filePath);
				FileOutputStream os = new FileOutputStream(outFile);

				if (SNAKE_YAML_SAVE)
				{
					org.yaml.snakeyaml.Yaml dumpEncoder = new org.yaml.snakeyaml.Yaml();
					dataString = dumpEncoder.dump(m_dataStorage);
				}
				else
				{
					StringBuilder sb = new StringBuilder();
					
				    Iterator it = m_dataStorage.entrySet().iterator();
				    
				    while (it.hasNext()) 
				    {
				        Map.Entry pairs = (Map.Entry)it.next();
				        
				        Object key = (Object) pairs.getKey();
				        Object val = pairs.getValue();
				        
				        saveSelector(sb, "", key.toString(), val);
				    }
				    
				    dataString = sb.toString();
				}
				
			    os.write(dataString.getBytes());
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
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

				sb.append("\"");
				sb.append(itemValue.toString());
				sb.append("\"");
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
	
	public void saveTo(String newPath) throws FileNotFoundException
	{
		m_filePath = newPath;
		save();
	}

	public Object get(String section) 
	{
		return m_dataStorage.get(section);
	}

	public void set(String sectionName, String param, String value) 
	{
		Map mSection = (Map) m_dataStorage.get(sectionName);
		
		if (null != mSection)
		{
			mSection.put(param, value);
		}
		else
		{
			LinkedHashMap m = new LinkedHashMap();
			m.put(param, value);
			m_dataStorage.put(sectionName, m);
		}
	}
}
