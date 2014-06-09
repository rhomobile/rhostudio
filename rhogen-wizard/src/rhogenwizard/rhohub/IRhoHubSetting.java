package rhogenwizard.rhohub;

public interface IRhoHubSetting
{
    public static final String rhoHubProxy                = "rho_hub_httpproxy";
    public static final String rhoHubSelectedPlatform     = "rho_hub_select_platform";
    public static final String rhoHubSelectedRhodesVesion = "rho_hub_select_rhodes_vesrion";
    public static final String isRhoHubLink               = "use_rho_hub";

    public boolean isLinking();
    //
    public String getToken();
    //
    public String getServerUrl();
    //
    public String getSelectedPlatform();
    //
    public String getRhodesBranch();
    //
    public String getAppBranch();
    //
    public String getHttpProxy();
}
