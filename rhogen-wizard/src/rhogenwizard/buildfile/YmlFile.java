package rhogenwizard.buildfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.ho.yaml.Yaml;

public class YmlFile
{
	private String m_filePath = null;
	private Map m_dataStorage = null;
	
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
		m_dataStorage = (Map) Yaml.load(ymlFile);		
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

	public String get(String sectionName)
	{
		String section = (String) m_dataStorage.get(sectionName);
		return section;
	}
	
	public void set(String sectionName, String value) throws FileNotFoundException 
	{
		m_dataStorage.put(sectionName, value);
		save();
	}
	
	private void save() throws FileNotFoundException
	{
		try 
		{
			org.yaml.snakeyaml.Yaml dumpEncoder = new org.yaml.snakeyaml.Yaml();
			String dataString = dumpEncoder.dump(m_dataStorage);
			
			File outFile = new File(m_filePath);
			FileOutputStream os = new FileOutputStream(outFile);
			os.write(dataString.getBytes());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
