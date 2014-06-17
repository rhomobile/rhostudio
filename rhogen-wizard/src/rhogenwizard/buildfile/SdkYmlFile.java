package rhogenwizard.buildfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SdkYmlFile extends YmlFile 
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
		super.set("env", "paths", "android-ndk", newNdkPath);
	}

	public String getAndroidSdkPath()
	{
		return super.get("env", "paths", "android");
	}
	
	public void setAndroidSdkPath(String newSdkPath)
	{
		super.set("env", "paths", "android", newSdkPath);
	}

	public String getJavaPath()
	{
		return super.get("env", "paths", "java");
	}
	
	public void setJavaPath(String newJavaPath)
	{
		super.set("env", "paths", "java", newJavaPath);
	}
	
	public String getCabWizPath()
	{
		return super.get("env", "paths", "cabwiz");
	}
	
	public void setCabWizPath(String newCabWizPath)
	{
		super.set("env", "paths", "cabwiz", newCabWizPath);
	}

	public String getVcBuildPath()
	{
		return super.get("env", "paths", "vcbuild");
	}
	
	public void setVcBuildPath(String newVcBuildPath)
	{
		super.set("env", "paths", "vcbuild", newVcBuildPath);
	}
	}
