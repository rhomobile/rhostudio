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
	
	public String getAndroidNdkPath()
	{
		return super.get("env", "paths", "android-ndk");
	}

	public void setAndroidNdkPath(String newNdkPath)
	{
		super.set("env", "paths", "android-ndk", (Object)newNdkPath);
	}

	public String getAndroidSdkPath()
	{
		return super.get("env", "paths", "android");
	}
	
	public void setAndroidSdkPath(String newSdkPath)
	{
		super.set("env", "paths", "android", (Object)newSdkPath);
	}

	public String getJavaPath()
	{
		return super.get("env", "paths", "java");
	}
	
	public void setJavaPath(String newJavaPath)
	{
		super.set("env", "paths", "java", (Object)newJavaPath);
	}
	
	public String getCabWizPath()
	{
		return super.get("env", "paths", "cabwiz");
	}
	
	public void setCabWizPath(String newCabWizPath)
	{
		super.set("env", "paths", "cabwiz", (Object)newCabWizPath);
	}
}
