package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

import rhogenwizard.PlatformType;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.WinMobileSdk;

public class CleanPlatformTask extends RubyExecTask
{
	   private static String[] getArgs(PlatformType platformType, String wmSdkVersion)
	    {
	        List<String> cmdLine = new ArrayList<String>();

	        cmdLine.add("rake");
	        cmdLine.add("clean:" + platformType.id);

	        if (platformType == PlatformType.eWm && wmSdkVersion != null)
	        {
	            cmdLine.add("rho_wm_sdk=" + WinMobileSdk.fromVersion(wmSdkVersion).sdkId);
	        }

	        return cmdLine.toArray(new String[0]);
	    }
   
    public CleanPlatformTask(String workDir, PlatformType platformType, String wmSdk)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, getArgs(platformType, wmSdk));
    }
}
