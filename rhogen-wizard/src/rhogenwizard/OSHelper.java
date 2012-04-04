package rhogenwizard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OSHelper extends OSValidator
{
	public static void killScriptProcess(String processName) throws Exception
	{
		killProcess(processName, processName + ".bat"); 
	}
	
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

	public static File concat(String... paths)
    {
        File file = new File("");
        for (int i = 0; i < paths.length; i++)
        {
            file = new File(file, paths[i]);
        }
        return file;
    }
}
