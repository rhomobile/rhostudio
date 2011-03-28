package rhogenwizard.buildfile;

import java.io.File;
import java.io.FileNotFoundException;

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
	
	public String getSdkPath()
	{
		return super.get("sdk");
	}
	
	public void setAppLog(String appLog) throws FileNotFoundException
	{
		super.set("applog", appLog);
	}
}
