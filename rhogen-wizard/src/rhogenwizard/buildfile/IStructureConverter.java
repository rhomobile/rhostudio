package rhogenwizard.buildfile;

import java.io.FileNotFoundException;
import java.util.Map;

public interface IStructureConverter 
{
	Map getDataStorage(String filaPath) throws FileNotFoundException;
	
	void applyDataStorage(Map dataStorage);
	
	String convertStructure();
}
