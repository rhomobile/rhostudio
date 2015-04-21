package rhogenwizard.sdk.task.run;

import org.eclipse.debug.core.ILaunch;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.task.RubyDebugTask;
import rhogenwizard.sdk.task.RubyExecTask;
import rhogenwizard.sdk.task.SeqDebugTask;

public class RunDebugRhoconnectAppTask extends SeqDebugTask
{
    public RunDebugRhoconnectAppTask(String workDir, String appName, ILaunch launch)
    {
        super(
            new RubyExecTask(workDir, SysCommandExecutor.RUBY_BAT,
                "rhoconnect", "redis-startbg"),
            new RubyDebugTask(launch, appName, workDir, SysCommandExecutor.RUBY_BAT,
                "rhoconnect", "startdebug")
        );
    }
}
