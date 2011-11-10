package rhogenwizard;

public enum PlatformType
{
	eWm,
	eAndroid,
	eBb,
	eIPhone,
	eWp7,
	eRsync,
	eUnknown;

	public static final String platformWinMobile  = "wm";
	public static final String platformAdroid     = "android";
	public static final String platformBlackBerry = "bb";
	public static final String platformIPhone     = "iphone";
	public static final String platformWp7        = "wp";
	public static final String platformRsync      = "";
		
	@Override
	public String toString() 
	{
		switch(this)
		{
		case eWm:
			return platformWinMobile;
		case eAndroid:
			return platformAdroid;
		case eBb:
			return platformBlackBerry;
		case eIPhone:
			return platformIPhone;
		case eWp7:
			return platformWp7;
		case eRsync:
			return platformRsync;
		}

		return null;
	}
	
	public static PlatformType fromString(String newPlatform)
	{
		if (newPlatform.equals(platformWinMobile))
		{
			return PlatformType.eWm;
		}
		else if (newPlatform.equals(platformAdroid))
		{
			return PlatformType.eAndroid;
		}
		else if (newPlatform.equals(platformBlackBerry))
		{
			return PlatformType.eBb;
		}
		else if (newPlatform.equals(platformIPhone))
		{
			return PlatformType.eIPhone;
		}
		else if (newPlatform.equals(platformWp7))
		{
			return PlatformType.eWp7;
		}
		else if (newPlatform.equals(platformRsync))
		{
			return PlatformType.eRsync;
		}
		
		return PlatformType.eUnknown;		
	}
}
