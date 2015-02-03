package rhogenwizard.sdk.task.liveupdate;

import org.eclipse.core.runtime.IProgressMonitor;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;

public class DiscoverTask extends RubyExecTask
{
	public DiscoverTask(String workDir, String subnetAddress) 
	{		
		super(workDir, SysCommandExecutor.RUBY_BAT, "rake");
	}
	
	@Override
	public void run(IProgressMonitor monitor)
	{
		// TODO Auto-generated method stub
		super.run(monitor);
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public boolean isDeviceFound()
	{
		return true;
	}
}
