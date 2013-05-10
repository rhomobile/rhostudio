package rhogenwizard.editors;

import java.util.ArrayList;
import java.util.List;

import rhogenwizard.PlatformType;

public enum Capabilities
{
	eUnknown(null, null),
	eGps("gps", PlatformType.eUnknown),
	ePim("pim", PlatformType.eUnknown),
	eCamera("camera", PlatformType.eUnknown),
	eVibrate("vibrate", PlatformType.eUnknown),
	ePhone("phone", PlatformType.eUnknown),
	eBluetooth("bluetooth", PlatformType.eUnknown),
	eCalendar("calendar", PlatformType.eUnknown),
	eNoMotoDevice("non_motorola_device", PlatformType.eUnknown),
	eNativeBrowser("native_browser", PlatformType.eUnknown),
	eMotoBrowser("motorola_browser", PlatformType.eUnknown),
	eHardAccelerate("hardware_acceleration", PlatformType.eAndroid),
        eNetWorkState("network_state",PlatformType.eAndroid),
	eSDCard("sdcard",PlatformType.eAndroid);
	
	public final String       publicId;
	public final PlatformType platformId;

	private Capabilities(final String publicName, final PlatformType platform)
	{
		platformId = platform;
		publicId   = publicName;
	}
	
    public static String[] getPublicIds()
    {
        return getPublicIdsList().toArray(new String[0]);
    }
    
    public static List<String> getPublicIdsList()
    {
        List<String> list = new ArrayList<String>();
        
        for (Capabilities pt : Capabilities.values())
        {
            if (pt.publicId != null)
            {
                list.add(pt.publicId);
            }
        }
        
        return list;
    }
    
    public static List<String> getPublicIdsList(List<Capabilities> capabList)
    {
        List<String> list = new ArrayList<String>();
        
        for (Capabilities pt : capabList)
        {
            if (pt.publicId != null)
            {
                list.add(pt.publicId);
            }
        }
        
        return list;
    }
    
    public static List<Capabilities> getCapabilitiesList(List<String> capabList)
    {
        List<Capabilities> list = new ArrayList<Capabilities>();
        
        if (capabList != null)
        {
	        for (String pt : capabList)
	        {
	            list.add(Capabilities.fromId(pt));
	        }
        }
        
        return list;
    }
    
    public static Capabilities fromId(String id)
    {
        for (Capabilities pt : Capabilities.values())
        {
            if (id.equals(pt.publicId))
            {
                return pt;
            }
        }
        
        return Capabilities.eUnknown;
    }
    
    @Override
    public String toString()
    {
        return publicId;
    }
};
