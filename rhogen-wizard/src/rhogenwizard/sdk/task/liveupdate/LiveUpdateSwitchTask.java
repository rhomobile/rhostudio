package rhogenwizard.sdk.task.liveupdate;

import org.eclipse.core.runtime.IPath;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;

public class LiveUpdateSwitchTask extends RubyExecTask
{
	public LiveUpdateSwitchTask(IPath iPath, boolean isEnable) 
	{
		super(iPath.toOSString(), SysCommandExecutor.RUBY_BAT, "rake", 
				isEnable == true ? "dev:update:auto" : "dev:update:auto_stop");	
	}
}
