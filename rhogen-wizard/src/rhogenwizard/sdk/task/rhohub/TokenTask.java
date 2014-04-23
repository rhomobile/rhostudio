package rhogenwizard.sdk.task.rhohub;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import rhogenwizard.DialogUtils;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;
import rhogenwizard.sdk.task.RunTask;

public class TokenTask extends RubyExecTask
{
    private static String[] getSetArgs(String newToken)
    {
        String task = "token:set[" + newToken + "]";

        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add("rake");
        cmdLine.add(task);
        
        return cmdLine.toArray(new String[0]);
    }

    private static String[] getGetArgs()
    {
        String task = "token:get";

        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add("rake");
        cmdLine.add(task);

        return cmdLine.toArray(new String[0]);
    }
    
    private static String[] getClearArgs()
    {
        String task = "token:clear";

        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add("rake");
        cmdLine.add(task);

        return cmdLine.toArray(new String[0]);
    }

    public TokenTask(String workDir, String newToken)
    {
    	super(workDir, SysCommandExecutor.RUBY_BAT, getSetArgs(newToken));
    }

    public TokenTask(String workDir)
    {
   		super(workDir, SysCommandExecutor.RUBY_BAT, getGetArgs());
    }

    public TokenTask(String workDir, boolean isClear)
    {
   		super(workDir, SysCommandExecutor.RUBY_BAT, getClearArgs());
    }

	public static String getToken(String workDir)
	{
		RubyExecTask task = new TokenTask(workDir);		
		task.run();

		String cmdOutput = task.getOutput();
				
		String[] lines = cmdOutput.split("\n");
		
		for (String line : lines)
		{
			if (line.indexOf("Token[") != -1)
			{
				String token = line.substring(line.indexOf("[") + 1);				
				token = token.substring(0, token.indexOf("]"));				
				return token;
			}
		}		
		
		return "";
	}
	
	public static void setToken(String workDir, String token)
	{
		RunTask task = new TokenTask(workDir, token);		
		task.run();	
	}
	
	public static void clearToken(String workDir)
	{
		RunTask task = new TokenTask(workDir, true);		
		task.run();	
	}
}
