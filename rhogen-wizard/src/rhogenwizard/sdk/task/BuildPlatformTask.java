package rhogenwizard.sdk.task;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.PlatformType;

public class BuildPlatformTask extends ARubyTask
{
    public BuildPlatformTask(String workDir, PlatformType platformType)
    {
        super(workDir, "rake", "device:" + platformType + ":production");
    }

    @Override
    protected void exec()
    {
        ConsoleHelper.showBuildConsole();
        super.exec();
    }
}
