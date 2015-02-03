package rhogenwizard.sdk.task.liveupdate;

import org.eclipse.core.runtime.IPath;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;

public class LiveUpdateSwitchTask extends RubyExecTask
{
	public LiveUpdateSwitchTask(IPath iPath, boolean isEnable) 
	{
		super(iPath.toOSString(), SysCommandExecutor.RUBY_BAT, "rake", 
				isEnable == true ? "discover:on" : "discover:off");	
	}

	@Override
	public boolean isOk() {
		// TODO Auto-generated method stub
		return true;//super.isOk();
	}
}
