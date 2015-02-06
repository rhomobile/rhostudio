package rhogenwizard.sdk.task.liveupdate;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;

public class DiscoverTask extends RubyExecTask
{
	public DiscoverTask(String workDir, String subnetAddress) 
	{		
		super(workDir, SysCommandExecutor.RUBY_BAT, "rake", "dev:network:discovery[" + subnetAddress + "]");
	}
}
