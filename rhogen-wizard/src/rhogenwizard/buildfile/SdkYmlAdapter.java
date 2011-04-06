package rhogenwizard.buildfile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rhogenwizard.OSValidator;
import rhogenwizard.SysCommandExecutor;

public class SdkYmlAdapter
{
	private static final String sdkPathSetupCommandUnix = "set-rhodes-sdk";
	private static final String sdkPathSetupCommandWin  = "set-rhodes-sdk.bat";
	
	private static String getPathToYaml() throws Exception
	{
		SysCommandExecutor executor = new SysCommandExecutor();
		
		List<String> cmdArgs = new ArrayList<String>();
		
		if (OSValidator.isWindows()) 
		{
			cmdArgs.add(sdkPathSetupCommandWin);
		}
		else 
		{
			cmdArgs.add(sdkPathSetupCommandUnix);
		}
		
		int ret = executor.runCommand(cmdArgs);
		
		if (ret != 0) 
		{
			return null;
		}
		
		String rawPath = executor.getCommandOutput();
		
		rawPath = rawPath.replaceAll("\\p{Cntrl}", "");  
		
		File rawFile = new File(rawPath);
		
		if (!rawFile.isDirectory()) 
		{
			return null;
		}
		
		File parentDir = rawFile.getParentFile();
		
		return parentDir.getAbsolutePath();
	}
	
	public static SdkYmlFile getRhobuildFile() throws Exception
	{
		String pathToRhodes = getPathToYaml();
		
		if (pathToRhodes != null)
		{
			SdkYmlFile ymlFile = new SdkYmlFile(pathToRhodes + File.separator + SdkYmlFile.configName);
		
			return ymlFile;
		}
		
		return null;
	}
	
	public static void setNewRhodesPath(String path) throws Exception
	{
		SysCommandExecutor executor = new SysCommandExecutor();
		
		List<String> cmdArgs = new ArrayList<String>();
		
		if (OSValidator.isWindows()) 
		{
			cmdArgs.add(sdkPathSetupCommandWin);
		}
		else 
		{
			cmdArgs.add(sdkPathSetupCommandUnix);
		}
		
		cmdArgs.add(path);
		
		executor.runCommand(cmdArgs);
	}
}
