package rhogenwizard;

public class CloudUtils
{
    public static String buildTask(PlatformType platformType)
    {
        return "cloud:build:" + platformType + ":" +
            ((platformType == PlatformType.eIPhone) ? "development" : "production");
    }

    public static String runTask(RunType runType)
    {
        return "cloud:run:" + runTarget(runType);
    }

    private static String runTarget(RunType runType)
    {
        switch (runType)
        {
        case eDevice:
            return "device";
        case eEmulator:
            return "simulator";
        }
        throw new IllegalArgumentException();
    }
}
