package rhogenwizard.buildfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;

import rhogenwizard.PlatformType;
import rhogenwizard.RunExeHelper;
import rhogenwizard.editors.Capabilities;

public final class AppYmlFile extends YmlFile 
{
	public static final String configFileName = "build.yml"; 

	public static AppYmlFile createFromProject(IProject project) throws FileNotFoundException 
	{
		if (project != null)
		{
			String projectPath = project.getLocation().toOSString();
			String projectFullPath = projectPath + File.separator + configFileName; 
			
			AppYmlFile ymlFile = new AppYmlFile(projectFullPath);
			
			if (ymlFile.getData() == null)
				return null;
			
			return ymlFile;
		}
		
		return null;
	}
	
	public static boolean isExists(String projectPath)
	{
		String buildFilePath = projectPath + File.separator + configFileName;
		File buildFile = new File(buildFilePath);
		return buildFile.exists();
	}
	
	public AppYmlFile(String ymlFileName) throws FileNotFoundException 
	{
		super(ymlFileName);
	}

	public AppYmlFile(File ymlFile) throws FileNotFoundException 
	{
		super(ymlFile);
	}
	
	public String getSdkConfigPath()
	{
		return getSdkPath() + File.separator + SdkYmlFile.configName;
	}
	
	public String getAppLog()
	{
		return super.getString("applog");
	}
	
	public String getSdkPath()
	{
		String sdkPath = (String) super.get("sdk");
		
		if (sdkPath == null)
		{
			sdkPath = RunExeHelper.getSdkInfo();
		}
		
		if (sdkPath == null)
			sdkPath = new String();
		
		return sdkPath; 
	}
	
	public String getFrameworkPath()
	{
		String sdkPath = getSdkPath();
		
		return sdkPath + File.separator + "lib" + File.separator + "framework";
	}
	
	public void setAppLog(String appLog)
	{
		super.set("applog", appLog);
	}
	
	public void setSdkPath(String sdkPath)
	{
		super.set("sdk", sdkPath);
	}
	
	public void setCapabilities(List<Capabilities> capList)
	{	
		List<String> allPlatfromCapabilities = new ArrayList<String>();
		List<String> androidCapabilities     = new ArrayList<String>();
		
		for(Capabilities c : capList)
		{
			if (c.platformId == PlatformType.eUnknown)
				allPlatfromCapabilities.add(c.toString());
			else
				androidCapabilities.add(c.toString());
		}
		 
		super.set("capabilities", allPlatfromCapabilities);		
		super.set("android", "capabilities", androidCapabilities);
	}
	
	public List<String> getGeneralExtension()
	{
		List<String> extList = (List<String>)super.getObject("extensions");
		
		if (extList == null)
			super.set("extensions", new ArrayList<String>());
			
		return (List<String>)super.getObject("extensions");
	}
	
	public void setGeneralExtension(List<String> extList)
	{
		super.set("extensions", extList);
	}
	
	public List<Capabilities> getCapabilities()
	{
		List<String> rawList     = (List<String>)super.getObject("capabilities");
		List<String> androidList = (List<String>)super.getObject("android", "capabilities");
		if(rawList != null) {
			if (androidList != null)
			{
				rawList.addAll(androidList);
			}
		}else{
			rawList = androidList;
		}
		
		return Capabilities.getCapabilitiesList(rawList);
	}
	
	public String getAppName()
	{
		return super.getString("name");
	}
	
	public void setAppName(String appName)
	{
		super.set("name", appName);
	}

	public String getAndroidVer() 
	{
		if (super.getObject("android", "version") != null)
		{
			return super.getObject("android", "version").toString();
		}
		
		return null;
	}

	public void setAndroidVer(String selVersion)
	{
		super.set("android", "version", selVersion);
	}

	public void setBbVer(String selVersion) 
	{
		super.set("bbver", selVersion);
	}

	public String getAndroidEmuName() 
	{
		return super.get("android", "emulator");
	}

	public void setAndroidEmuName(String newName)
	{
		super.set("android", "emulator", newName);
	}

	public String getIphoneVer() 
	{
		return super.get("iphone", "emulatortarget");
	}
	
	public void setIphoneVer(String iphoneTarget) 
	{
		super.set("iphone", "emulatortarget", iphoneTarget);
	}

	public void removeAndroidEmuName() 
	{
		remove("android", "emulator");
	}

	public boolean isRhoelements()
	{
		String appType = super.getString("app_type");
		
		if (appType == null)
			return false;
			
		return appType.equals("rhoelements");
	}

	public void enableRhoelementsFlag()
	{
		super.set("app_type", "rhoelements");
	}
	
	public void disableRhoelementsFlag()
	{
		super.remove("app_type", "rhodes");
	}
	
	public void setWmSdk(String sdk)
	{
		super.set("wm", "sdk", sdk);
	}
	
	public String getWmSdk()
	{
		return super.get("wm", "sdk");
	}
}
