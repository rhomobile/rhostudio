package rhogenwizard.sdk.task;

import rhogenwizard.SysCommandExecutor;

public class CleanCloudCacheTask extends RubyExecTask
{
    public CleanCloudCacheTask(String workDir)
    {
        super(workDir, SysCommandExecutor.RUBY_BAT, "rake", "cloud:cache:clear");
    }
}
