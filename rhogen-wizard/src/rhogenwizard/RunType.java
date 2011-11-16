package rhogenwizard;

public enum RunType 
{
	eUnknow,
	eDevice,
	eEmulator,
	eRhoEmulator;

	/* platforms types */
	public static final String platformRhoSim = "RhoSimulator";
	public static final String platformSim    = "Simulator";
	public static final String platformDevice = "Device";
	
	@Override
	public String toString()
	{
		switch(this)
		{
		case eDevice:
			return platformDevice.toLowerCase();
		case eEmulator:
			return platformSim.toLowerCase();
		case eRhoEmulator:
			return platformRhoSim.toLowerCase();
		}
		
		return "";
	}
	
	public static RunType fromString(String newType)
	{
		if (platformRhoSim.toLowerCase().equals(newType.toLowerCase()))
		{
			return RunType.eRhoEmulator;
		}
		else if (platformSim.toLowerCase().equals(newType.toLowerCase()))
		{
			return RunType.eEmulator;
		}
		else if (platformDevice.toLowerCase().equals(newType.toLowerCase()))
		{
			return RunType.eDevice;
		}
		
		return eUnknow;
	}
}
