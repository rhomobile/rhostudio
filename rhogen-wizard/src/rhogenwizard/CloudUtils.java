package rhogenwizard;

public class CloudUtils
{
    public static String buildTask(PlatformType platformType)
    {
        return "cloud:build:" + platformType + ":" +
            ((platformType == PlatformType.eIPhone) ? "development" : "production");
    }
}
