package rhogenwizard.buildfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.ho.yaml.Yaml;
import org.ho.yaml.YamlConfig;
import org.omg.CORBA.portable.OutputStream;
import org.yaml.snakeyaml.Dumper;

class MyConfig extends YamlConfig
{
};

public class YmlFile
{
	private String m_filePath = null;
	private Map m_dataStorage = null;
	
	public YmlFile(String ymlFileName) throws FileNotFoundException
	{
		m_filePath = ymlFileName;
		m_dataStorage = (Map) Yaml.load(new File(ymlFileName));
	}
	
	public YmlFile(File ymlFile) throws FileNotFoundException
	{
		m_filePath = ymlFile.getAbsolutePath();
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
