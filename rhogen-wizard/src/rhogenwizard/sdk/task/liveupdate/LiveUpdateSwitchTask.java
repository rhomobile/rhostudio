package rhogenwizard.sdk.task.liveupdate;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;

public class LiveUpdateSwitchTask extends RubyExecTask
{
	public LiveUpdateSwitchTask(String workDir, boolean isEnable) 
	{
		super(workDir, SysCommandExecutor.RUBY_BAT, 
				isEnable == true ? "discover:on" : "discover:off");	
	}
}
