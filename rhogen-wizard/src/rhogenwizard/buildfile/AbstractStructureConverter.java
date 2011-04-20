package rhogenwizard.buildfile;

import java.util.Map;

public abstract class AbstractStructureConverter implements IStructureConverter 
{
	Map m_dataStructure = null;
	
	@Override
	public void applyDataStirage(Map dataStorage) 
	{
		m_dataStructure = dataStorage;
	}
}
