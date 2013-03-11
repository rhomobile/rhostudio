package rhogenwizard.buildfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import rhogenwizard.buildfile.converter.CustomConverter;
import rhogenwizard.buildfile.converter.IStructureConverter;

public class YmlFile
{
	private String              m_filePath = null;
	private Map<Object, Object> m_dataStorage = null;
	private IStructureConverter m_dataConverter = new CustomConverter();
	
	public YmlFile(String ymlFileName) throws FileNotFoundException
	{
		m_filePath = ymlFileName;
		File ymlFile = new File(ymlFileName);
		
		if (ymlFile.exists()) 
		{
			load(ymlFileName);
		}
	}
	
	public YmlFile(File ymlFile) throws FileNotFoundException
	{
		m_filePath = ymlFile.getAbsolutePath();
		load(m_filePath);
	}
	
	public String getPath()
	{
		return m_filePath;
	}
	
	public Map<?, ?> getData()
	{
		return m_dataStorage;
	}
	
	private void load(String ymlFilePath) throws FileNotFoundException
	{
		m_dataStorage = m_dataConverter.getDataStorage(ymlFilePath);
	}
	
	public String get(String sectionName, String paramName)
	{
		Map<?, ?> section = (Map<?, ?>) m_dataStorage.get(sectionName);
		
		if (null != section)
		{
			return (String) section.get(paramName);
		}
		
		return null;
	}
	
	public void remove(String sectionName, String paramName)
	{
		Map<?, ?> section = (Map<?, ?>) m_dataStorage.get(sectionName);
		
		if (null != section)
		{
			section.remove(paramName);
		}
	}
	
	public String get(String mainSection, String sectionName, String paramName)
	{
		Map<?, ?> mSection = (Map<?, ?>) m_dataStorage.get(mainSection);
		
		if (null != mSection)
		{
			Map<?, ?> section = (Map<?, ?>) mSection.get(sectionName);
			
			if (null != section)
			{
				return (String) section.get(paramName);
			}
		}
		
		return null;
	}
	

	public Object getObject(String mainSection, String sectionName)
	{
		Map<?, ?> mSection = (Map<?, ?>) m_dataStorage.get(mainSection);
		
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
			Map<?, ?> comSection =  (Map<?, ?>) m_dataStorage.get(commonSection);
			
			if (comSection == null)
				return null;
			
			Map<?, ?> mSection = (Map<?, ?>) comSection.get(mainSection);
			
			if (null != mSection)
			{
				Map<?, ?> section = (Map<?, ?>) mSection.get(sectionName);
				
				if (null != section)
				{
					return section.get(paramName).toString();
				}
				
				section = (Map<?, ?>) mSection.get(new Double(sectionName));
				
				if (null != section)
				{
					return section.get(paramName).toString();
				}
			}
		}
		catch(Exception e) {
		}

		return null;
	}
	
	public boolean set(String commonSection, String mainSection, Object sectionName, String paramName, Object value)
	{
		try
		{
			Map<?, ?> comSection =  (Map<?, ?>) m_dataStorage.get(commonSection);
			
			if (comSection != null)
			{			
			    Map mSection = (Map<?, ?>) comSection.get(mainSection);
				
				if (null != mSection)
				{
					Map section = (Map<?, ?>) mSection.get(sectionName);
					
					if (null != section)
					{
						section.put(paramName, value);
					}
					else
					{
						LinkedHashMap<Object, Object> m = new LinkedHashMap<Object, Object>();
						m.put(paramName, value);
						mSection.put(sectionName, m);
					}
				}
			}
		}
		catch(Exception e) 
		{
			return false;
		}
		
		return true;
	}
	
	public String getString(String sectionName)
	{
		String section = (String) m_dataStorage.get(sectionName);
		return section;
	}
	
	public Object getObject(String sectionName)
	{
		Object section = m_dataStorage.get(sectionName);
		return section;
	}

	public void set(String mainSection, String subSection, String paramName, Object value) 
	{
		Map<?, ?> mSection = (Map<?, ?>) m_dataStorage.get(mainSection);
		
		if (null != mSection)
		{
			Map section = (Map<?, ?>) mSection.get(subSection);
			
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
	
	public void set(String mainSection, String paramName, Object value) 
	{
		Map mSection = (Map<?, ?>) m_dataStorage.get(mainSection);
		
		if (null != mSection)
		{
			mSection.put(paramName, value);
		}
	}
		
	public void save()
	{
		try 
		{
			if (m_filePath.length() != 0)
			{
				String dataString = null;

				File outFile = new File(m_filePath);
				FileOutputStream os = new FileOutputStream(outFile);

				m_dataConverter.applyDataStorage(m_dataStorage);
				dataString = m_dataConverter.convertStructure();
				
			    os.write(dataString.getBytes());			    
			    os.close();
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void saveTo(String newPath)
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
		Map mSection = (Map<?, ?>) m_dataStorage.get(sectionName);
		
		if (null != mSection)
		{
			mSection.put(param, value);
		}
		else
		{
			LinkedHashMap<Object, Object> m = new LinkedHashMap<Object, Object>();
			m.put(param, value);
			m_dataStorage.put(sectionName, m);
		}
	}
}
