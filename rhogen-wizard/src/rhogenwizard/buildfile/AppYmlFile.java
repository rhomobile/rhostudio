package rhogenwizard.buildfile;

import java.io.File;
import java.io.FileNotFoundException;

public class AppYmlFile extends YmlFile 
{
	public AppYmlFile(String ymlFileName) throws FileNotFoundException 
	{
		super(ymlFileName);
	}

	public AppYmlFile(File ymlFile) throws FileNotFoundException 
	{
		super(ymlFile);
	}
	
	public String getAppLog()
	{
		return super.get("applog");
	}
}
