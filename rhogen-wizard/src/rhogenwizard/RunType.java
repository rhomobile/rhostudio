package rhogenwizard;

import java.util.ArrayList;
import java.util.List;

public enum RunType 
{
	eUnknown(null, null),
	eDevice("device", "Device"),
	eEmulator("simulator", "Simulator"),
	eRhoSimulator("rhosimulator", "RhoSimulator");

    public final String id;
	public final String publicId;
	
	private RunType(String id, String publicId)
	{
        this.id = id;
	    this.publicId = publicId;
	}
	
    public static String[] getPublicIds()
    {
        List<String> list = new ArrayList<String>();

        for (RunType rt : values())
        {
            if (rt.publicId != null)
            {
                list.add(rt.publicId);
            }
        }

        return list.toArray(new String[0]);
    }

    public static RunType fromId(String id)
    {
        if (id != null)
        {
            for (RunType rt : values())
            {
                if (id.equals(rt.id))
                {
                    return rt;
                }
            }
        }

        return eUnknown;
    }

	public static RunType fromPublicId(String publicId)
	{
        for (RunType rt : values())
        {
            if (publicId.equals(rt.publicId))
            {
                return rt;
            }
        }

        return eUnknown;
	}
}
