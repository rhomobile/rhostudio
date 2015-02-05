package rhogenwizard.sdk.task.liveupdate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.SysCommandExecutor.Decorator;
import rhogenwizard.sdk.task.RubyExecTask;

public class PrintSubnetsTask extends RubyExecTask 
{
	public PrintSubnetsTask(String workDir)
	{
		super(workDir, SysCommandExecutor.RUBY_BAT, "rake", "dev:network:list");
	}
	
	public List<String> getSubnets()
	{
		String out = getOutput();
		
		List<String> subnetsList = Arrays.asList(out.split(";"));
		
		return subnetsList;
	}
} 
