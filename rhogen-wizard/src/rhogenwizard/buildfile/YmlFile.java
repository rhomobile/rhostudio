package rhogenwizard.buildfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class YmlFile
{
	private String m_filePath = null;
	private Map m_dataStorage = null;
	
	public YmlFile()
	{}
	
	public YmlFile(String ymlFileName) throws FileNotFoundException
	{
		m_filePath = ymlFileName;
		File ymlFile = new File(ymlFileName);
		load(ymlFile);
	}
	
	public YmlFile(File ymlFile) throws FileNotFoundException
	{
		m_filePath = ymlFile.getAbsolutePath();
		load(ymlFile);
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
				org.yaml.snakeyaml.Yaml dumpEncoder = new org.yaml.snakeyaml.Yaml();
				String dataString = dumpEncoder.dump(m_dataStorage);
				
				File outFile = new File(m_filePath);
				FileOutputStream os = new FileOutputStream(outFile);
				os.write(dataString.getBytes());
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void saveTo(String newPath) throws FileNotFoundException
	{
		m_filePath = newPath;
		save();
	}
}
