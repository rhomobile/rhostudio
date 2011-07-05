package rhogenwizard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OSHelper extends OSValidator
{
	public static void killProcess(String processName) throws Exception
	{
		killProcess(processName, processName + ".exe"); 
	}
	
	public static void killProcess(String unixName, String wndName) throws Exception
	{
		List<String> cmdLine = new ArrayList<String>();
		
		if (OSValidator.OSType.WINDOWS == OSValidator.detect()) 
		{
			cmdLine.add("taskkill.exe");
			cmdLine.add("/F");
			cmdLine.add("/IM");
			cmdLine.add(wndName);
		}
		else
		{
			cmdLine.add("killall");
			cmdLine.add("-9");
			cmdLine.add(unixName);
		}
		
		SysCommandExecutor executor = new SysCommandExecutor();
		executor.runCommand(cmdLine);
	}
	
	public static void deleteFolder(String pathToRootFolder)
	{
		File rootFolder = new File(pathToRootFolder);
		
		deleteFolder(rootFolder);
	}
	
	public static void deleteFolder(File rootFolder)
	{
		if (!rootFolder.isDirectory())
		{
			rootFolder.delete();
			return;
		}
		
		File[] containFiles = rootFolder.listFiles();
		
		for (File currFile : containFiles)
		{
			deleteFolder(currFile);
		}
		
		rootFolder.delete();
	}
	
	public static void setEnvVariable(String envName, String envValue) throws Exception
	{
		List<String> cmdLine = new ArrayList<String>();
		
		if (OSValidator.OSType.WINDOWS == OSValidator.detect()) 
		{
			System.setProperty(envName, envValue);
			
//			cmdLine.add("set.exe");
//			cmdLine.add(envName + "=" + envValue);
		}
		else
		{
			cmdLine.add("export");
			cmdLine.add(envName + "=" + envValue);
		}
		
//		SysCommandExecutor executor = new SysCommandExecutor();
//		executor.runCommand(cmdLine);
	}
}
