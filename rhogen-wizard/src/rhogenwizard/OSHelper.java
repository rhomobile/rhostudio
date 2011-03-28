package rhogenwizard;

import java.util.ArrayList;
import java.util.List;

public class OSHelper extends OSValidator
{
	public static void killProcess(String unixName, String wndName) throws Exception
	{
		List<String> cmdLine = new ArrayList<String>();
		
		if (OSValidator.OSType.WINDOWS == OSValidator.detect()) 
		{
			cmdLine.add("taskkill");
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
}
