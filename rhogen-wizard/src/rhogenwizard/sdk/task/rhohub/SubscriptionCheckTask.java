package rhogenwizard.sdk.task.rhohub;

import java.util.ArrayList;
import java.util.List;

import rhogenwizard.DialogUtils;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyExecTask;
import rhogenwizard.sdk.task.RunTask;

public class SubscriptionCheckTask extends RubyExecTask
{
    private static String[] getArgs()
    {
        String task = "license:check";

        List<String> cmdLine = new ArrayList<String>();
        cmdLine.add("rake");
        cmdLine.add(task);

        return cmdLine.toArray(new String[0]);
    }

    public SubscriptionCheckTask(String workDir)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, getArgs());
    }

	public static boolean checkRhoHubLicense(String workDir)
	{
		RunTask task = new SubscriptionCheckTask(workDir);		
		task.run();

		if (!task.isOk())
		{
			DialogUtils.error("License subscription", "RhoHub license subscription was not found.");
		}
		
		return task.isOk();
	}
}
