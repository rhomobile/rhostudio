package rhogenwizard;

import java.util.ArrayList;
import java.util.List;

public enum PlatformType
{
    eWm("wm", "Windows Mobile / Windows CE"),
    eAndroid("android", "Android"),
    eIPhone("iphone", "iPhone"),
    eUWP("uwp", "Windows Phone 10"),
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
        
        for (PlatformType pt : values())
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
        if (id != null)
        {
            for (PlatformType pt : values())
            {
                if (id.equals(pt.id))
                {
                    return pt;
                }
            }
        }
        
        return eUnknown;
    }

    public static PlatformType fromPublicId(String publicId)
    {
        for (PlatformType pt : values())
        {
            if (publicId.equals(pt.publicId))
            {
                return pt;
            }
        }
        
        return eUnknown;
    }
}
