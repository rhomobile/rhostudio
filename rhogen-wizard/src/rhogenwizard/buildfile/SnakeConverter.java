package rhogenwizard.buildfile;

public class SnakeConverter extends AbstractStructureConverter
{
	@Override
	public String convertStructure() 
	{
		org.yaml.snakeyaml.Yaml dumpEncoder = new org.yaml.snakeyaml.Yaml();
		return dumpEncoder.dump(m_dataStructure);
	}
}
