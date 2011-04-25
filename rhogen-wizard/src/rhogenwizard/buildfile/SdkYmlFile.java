package rhogenwizard.buildfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	public String getBbJdkPath(String version)
	{
		return super.get("env", "paths", version, "jde");
	}

	public void setBbJdkPath(String version, String value)
	{
		try
		{
			super.set("env", "paths", new Double(version), "jde", value);
		}
		catch(NumberFormatException e)
		{
			super.set("env", "paths", version, "jde", value);
		}
	}
	
	public String getBbMdsPath(String version) 
	{
		return super.get("env", "paths", version, "mds");
	}
	
	public void setBbMdsPath(String version, String value)
	{
		try
		{
			super.set("env", "paths", new Double(version), "mds", value);
		}
		catch(NumberFormatException e)
		{
			super.set("env", "paths", version, "mds", value);
		}
	}

	public String getBbSimPort(String version) 
	{
		return super.get("env", "paths", version, "sim");
	}
	
	public void setBbSimPort(String version, Integer value)
	{
		try
		{
			super.set("env", "paths", new Double(version), "sim", value);
		}
		catch(NumberFormatException e)
		{
			super.set("env", "paths", version, "sim", value);
		}
	}
	
	public List<String> getBbVersions()
	{
		List<String> versions = new ArrayList<String>();
		
		Map pathItems = (Map) super.getObject("env", "paths");
		
		Set keys = pathItems.keySet();
		
		for (Object s : keys)
		{
			Object item = pathItems.get(s);
			
			if (item instanceof Map)
			{
				Map mapItem = (Map)item;
				
				if (mapItem.get("mds") != null)
				{
					versions.add(s.toString());
				}
			}
		}
		
		Collections.sort(versions, String.CASE_INSENSITIVE_ORDER);
		
		return versions;
	}
}
