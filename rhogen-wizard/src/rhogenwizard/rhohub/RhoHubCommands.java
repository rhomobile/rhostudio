package rhogenwizard.rhohub;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;

public class RhoHubCommands
{
	public static String getToken(String workDir)
	{
		RubyExecTask task = new RubyExecTask(
		    workDir, SysCommandExecutor.RUBY_BAT, "rake", "token:get");
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
		new RubyExecTask(
		    workDir, SysCommandExecutor.RUBY_BAT, "rake", "token:set[" + token + "]").run();	
	}
	
    public static void login(String workDir, String username, String password)
    {
        new RubyExecTask(workDir, SysCommandExecutor.RUBY_BAT, "rake", "token:login")
        .input(username + '\n' + password + '\n')
        .run();    
    }
    
	public static void logout(String workDir)
	{
		new RubyExecTask(workDir, SysCommandExecutor.RUBY_BAT, "rake", "token:clear").run();	
	}
}
