package rhogenwizard.buildfile.converter;

import java.util.Map;


public abstract class AbstractStructureConverter implements IStructureConverter 
{
	Map<Object, Object> m_dataStructure = null;
	
	@Override
	public void applyDataStorage(Map<Object, Object> dataStorage)
	{
		m_dataStructure = dataStorage;
	}
}
