package rhogenwizard;

public enum PlatformType
{
    eWm("wm", "Windows Mobile"),
    eAndroid("android", "Android"),
    eBb("bb", "BlackBerry"),
    eIPhone("iphone", "iPhone"),
    eWp7("wp", "Windows Phone"),
    eSymbian("symbian", "Symbian"),
    eRsync("", ""),
    eUnknown(null, null);

    public final String id;
    public final String publicId;

    private PlatformType(String id, String publicId)
    {
        assert id.equals(id.toLowerCase());

        this.id = id;
        this.publicId = publicId;
    }

    @Override
    public String toString()
    {
        return id;
    }

    public static PlatformType fromString(String newPlatform)
    {
        String id = newPlatform.toLowerCase();
        for (PlatformType pt : PlatformType.values())
        {
            if (id.equals(pt.id) || id.equals(pt.publicId.toLowerCase()))
            {
                return pt;
            }
        }
        return PlatformType.eUnknown;
    }
}
