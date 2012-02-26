package rhogenwizard.buildfile.converter;

import java.util.Map;


public abstract class AbstractStructureConverter implements IStructureConverter 
{
	Map m_dataStructure = null;
	
	@Override
	public void applyDataStorage(Map dataStorage) 
	{
		m_dataStructure = dataStorage;
	}
}
