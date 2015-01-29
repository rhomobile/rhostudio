package rhogenwizard.sdk.task.liveupdate;

import java.util.ArrayList;
import java.util.List;

import rhogenwizard.SysCommandExecutor;
import rhogenwizard.SysCommandExecutor.Decorator;
import rhogenwizard.sdk.task.RubyExecTask;

public class PrintSubnetsTask extends RubyExecTask 
{
	public PrintSubnetsTask(String workDir)
	{
		super(workDir, SysCommandExecutor.RUBY_BAT, ".");
	}
	
	public List<String> getSubnets()
	{
		List<String> fakeList = new ArrayList<String>();
		fakeList.add("127.0.0.*");
		fakeList.add("250.0.0.*");
		fakeList.add("100.0.0.*");
		
		return fakeList;
	}
} 
