package rhogenwizard;

import java.util.ArrayList;
import java.util.List;

public enum PlatformType
{
    eWm("wm", "Windows Mobile"),
    eWCE("wince", "Windows CE"),
    eAndroid("android", "Android"),
    eIPhone("iphone", "iPhone"),
    eWp7("wp8", "Windows Phone"),    
    eWin32("win32", "Win32"),
    eRsync("", null),
    eUnknown(null, null);

    public final String id;
    public final String publicId;

    private PlatformType(String id, String publicId)
    {
        assert id.equals(id.toLowerCase());

        this.id = id;
        this.publicId = publicId;
    }

    public static String[] getPublicIds()
    {
        List<String> list = new ArrayList<String>();
        
        for (PlatformType pt : PlatformType.values())
        {
            if (pt.publicId != null)
            {
                list.add(pt.publicId);
            }
        }
        
        return list.toArray(new String[0]);
    }

    @Override
    public String toString()
    {
        return id;
    }

    public static PlatformType fromId(String id)
    {
        for (PlatformType pt : PlatformType.values())
        {
            if (id.equals(pt.id))
            {
                return pt;
            }
        }
        
        return PlatformType.eUnknown;
    }

    public static PlatformType fromPublicId(String publicId)
    {
        for (PlatformType pt : PlatformType.values())
        {
            if (publicId.equals(pt.publicId))
            {
                return pt;
            }
        }
        
        return PlatformType.eUnknown;
    }
}
