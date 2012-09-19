package rhogenwizard;

import java.util.ArrayList;
import java.util.List;

public enum WinMobileSdk
{
    v6_0("6.0", "Windows Mobile 6 Professional SDK (ARMV4I)"),
    v6_5_3("6.5.3", "Windows Mobile 6.5.3 Professional DTK (ARMV4I)");
    
    // it's comment because wince moved to separate platform
    //vCE_5_0("CE5.0", "MC3000c50b (ARMV4I)"); 

    public final String version;
    public final String sdkId;

    private WinMobileSdk(String version, String sdkId)
    {
        this.version = version;
        this.sdkId   = sdkId;
    }

    public static String[] getVersions()
    {
        List<String> versions = new ArrayList<String>();
        
        for (WinMobileSdk sdk : WinMobileSdk.values())
        {
            versions.add(sdk.version);
        }
        
        return versions.toArray(new String[0]);
    }
    
    public static WinMobileSdk fromVersion(String version)
    {
        for (WinMobileSdk sdk : WinMobileSdk.values())
        {
            if (version.equals(sdk.version)) 
            {
                return sdk;
            }
        }
        
        throw new IllegalArgumentException("Unknown version [" + version + "]");
    }
}
