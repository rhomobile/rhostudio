package rhogenwizard;

import java.util.ArrayList;
import java.util.List;

public enum BuildType
{
    eLocal("local", "Local"),
    eRhoMobileCom("rhomobile.com", "RhoMobile.com"),
    eUnknown(null, null);

    public final String id;
    public final String publicId;

    private BuildType(String id, String publicId)
    {
        assert id.equals(id.toLowerCase());

        this.id = id;
        this.publicId = publicId;
    }

    public static String[] getPublicIds()
    {
        List<String> list = new ArrayList<String>();
        
        for (BuildType bt : values())
        {
            if (bt.publicId != null)
            {
                list.add(bt.publicId);
            }
        }
        
        return list.toArray(new String[0]);
    }

    @Override
    public String toString()
    {
        return id;
    }

    public static BuildType fromId(String id)
    {
        for (BuildType bt : values())
        {
            if (id.equals(bt.id))
            {
                return bt;
            }
        }
        
        return BuildType.eUnknown;
    }

    public static BuildType fromPublicId(String publicId)
    {
        for (BuildType bt : values())
        {
            if (publicId.equals(bt.publicId))
            {
                return bt;
            }
        }
        
        return BuildType.eUnknown;
    }
}
