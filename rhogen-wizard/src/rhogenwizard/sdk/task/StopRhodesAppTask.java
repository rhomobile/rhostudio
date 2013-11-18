package rhogenwizard.sdk.task;

import java.util.ArrayList;
import java.util.List;

import rhogenwizard.PlatformType;
import rhogenwizard.RunType;
import rhogenwizard.SysCommandExecutor;

public class StopRhodesAppTask extends RubyExecTask
{
    private static final RunTask[] empty = {};

    private static String[] getArgs(PlatformType platformType, RunType runType)
    {      
        String task;       	
    	task = "stop:" + platformType.id + ":debug:";
    
    	switch(runType)
    	{
    	case eDevice:
    		task += "device";
    		break;
    	case eEmulator:
    		task += "emulator";
    	}

        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add("rake");
        cmdLine.add(task);

        return cmdLine.toArray(new String[0]);
    }
    
    public StopRhodesAppTask(String workDir, PlatformType platformType, RunType runType)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, getArgs(platformType, runType));
    }
}
