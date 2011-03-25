package rhogenwizard.buildfile;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.ho.yaml.Yaml;
import org.ho.yaml.YamlConfig;

public class YmlFile
{
	private String m_filePath = null;
	private Map m_dataStorage = null;
	
	public YmlFile(String ymlFileName) throws FileNotFoundException
	{
		m_dataStorage = (Map) Yaml.load(new File(ymlFileName));
	}
	
	public YmlFile(File ymlFile) throws FileNotFoundException
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
}
