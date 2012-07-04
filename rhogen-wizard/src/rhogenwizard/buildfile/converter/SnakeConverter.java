package rhogenwizard.buildfile.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;


public class SnakeConverter extends AbstractStructureConverter
{
	@Override
	public String convertStructure() 
	{
		org.yaml.snakeyaml.Yaml dumpEncoder = new org.yaml.snakeyaml.Yaml();
		return dumpEncoder.dump(m_dataStructure);
	}

	@Override
	public Map<Object, Object> getDataStorage(String filePath) throws FileNotFoundException 
	{
		File      ymlFile = new File(filePath);
		Yaml       yaml   = new Yaml();		
		FileReader fr     = new FileReader(ymlFile);

		return (Map<Object, Object>) yaml.load(fr);		
	}
}
