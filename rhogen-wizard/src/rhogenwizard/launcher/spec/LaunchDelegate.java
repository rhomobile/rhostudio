package rhogenwizard.launcher.spec;

public class LaunchDelegate extends rhogenwizard.launcher.LaunchDelegateBase
{
    public LaunchDelegate()
    {
        super("/app/SpecRunner", new String[] { "mspec", "fileutils" });
    }
}
