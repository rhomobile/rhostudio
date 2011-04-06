package rhogenwizard.buildfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;

public class AppYmlFile extends YmlFile 
{
	public static final String configFileName = "build.yml"; 

	public static AppYmlFile createFromProject(IProject project) throws FileNotFoundException 
	{
		String projectPath = project.getLocation().toOSString();
		String projectFullPath = projectPath + "/" + configFileName; 
		
		return new AppYmlFile(projectFullPath);
	}

	public static AppYmlFile createFromString(String data) 
	{
		AppYmlFile ymlFile = new AppYmlFile();
		ymlFile.fromString(data);
		return ymlFile;
	}

	public AppYmlFile() 
	{		
	}
	
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
		return super.getString("applog");
	}
	
	public String getSdkPath()
	{
		return super.getString("sdk");
	}
	
	public void setAppLog(String appLog)
	{
		super.set("applog", appLog);
	}
	
	public void setSdkPath(String sdkPath)
	{
		super.set("sdk", sdkPath);
	}
	
	public void setCapabilities(List<String> capList)
	{
		super.set("capabilities", capList);
	}
	
	public List<String> getCapabilities()
	{
		return (List<String>)super.getObject("capabilities");
	}
	
	public String getAppName()
	{
		return super.getString("name");
	}
	
	public void setAppName(String appName)
	{
		super.set("name", appName);
	}
}
