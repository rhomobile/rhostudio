package rhogenwizard.buildfile.converter;

import java.io.FileNotFoundException;
import java.util.Map;

public interface IStructureConverter 
{
	Map<Object, Object> getDataStorage(String filaPath) throws FileNotFoundException;
	
	void applyDataStorage(Map<Object, Object> dataStorage);
	
	String convertStructure();
}
