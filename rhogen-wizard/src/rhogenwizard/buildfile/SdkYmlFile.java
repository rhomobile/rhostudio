package rhogenwizard.buildfile;

import java.io.File;
import java.io.FileNotFoundException;

public class SdkYmlFile extends YmlFile 
{
	public static final String configName = "rhobuild.yml";
	
	public SdkYmlFile(File ymlFile) throws FileNotFoundException
	{
		super(ymlFile);
	}
	
	public SdkYmlFile(String ymlFileName) throws FileNotFoundException
	{
		super(ymlFileName);
	}

	public String getAppName()
	{
		return super.get("env", "app");
	}	
}
