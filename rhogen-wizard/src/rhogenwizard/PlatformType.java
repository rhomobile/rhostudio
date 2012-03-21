package rhogenwizard;

public enum PlatformType
{
	eWm,
	eAndroid,
	eBb,
	eIPhone,
	eWp7,
	eSymbian,
	eRsync,
	eUnknown;

	public static final String platformWinMobile  = "wm";
	public static final String platformAdroid     = "android";
	public static final String platformBlackBerry = "bb";
	public static final String platformIPhone     = "iphone";
	public static final String platformWp7        = "wp";
	public static final String platformSymbian    = "symbian";
	public static final String platformRsync      = "";
		
	public static final String platformWinMobilePublic  = "Windows Mobile";
	public static final String platformAdroidPublic     = "Android";
	public static final String platformBlackBerryPublic = "BlackBerry";
	public static final String platformIPhonePublic     = "iPhone";
	public static final String platformWp7Public        = "Windows Phone";
	public static final String platformSymbianPublic    = "Symbian";
	
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
		case eSymbian:
			return platformSymbian;
		case eRsync:
			return platformRsync;
		}

		return null;
	}
	
	public static PlatformType fromString(String newPlatform)
	{
		if (newPlatform.toLowerCase().equals(platformWinMobile) || newPlatform.toLowerCase().equals(platformWinMobilePublic.toLowerCase()))
		{
			return PlatformType.eWm;
		}
		else if (newPlatform.toLowerCase().equals(platformAdroid) || newPlatform.toLowerCase().equals(platformAdroidPublic.toLowerCase()))
		{
			return PlatformType.eAndroid;
		}
		else if (newPlatform.toLowerCase().equals(platformBlackBerry) || newPlatform.toLowerCase().equals(platformBlackBerryPublic.toLowerCase()))
		{
			return PlatformType.eBb;
		}
		else if (newPlatform.toLowerCase().equals(platformIPhone) || newPlatform.toLowerCase().equals(platformIPhonePublic.toLowerCase()))
		{
			return PlatformType.eIPhone;
		}
		else if (newPlatform.toLowerCase().equals(platformWp7) || newPlatform.toLowerCase().equals(platformWp7Public.toLowerCase()))
		{
			return PlatformType.eWp7;
		}
		else if (newPlatform.toLowerCase().equals(platformRsync))
		{
			return PlatformType.eRsync;
		}
		else if (newPlatform.toLowerCase().equals(platformSymbian) || newPlatform.toLowerCase().equals(platformSymbianPublic.toLowerCase()))
		{
			return PlatformType.eSymbian;
		}
				
		return PlatformType.eUnknown;		
	}
}
