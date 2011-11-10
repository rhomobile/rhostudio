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
	
/*	
	public RunType(ERunType newType)
	{
		m_type = newType;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof RunType)
		{
			if (((RunType) obj).getType() == m_type)
				return false;
		}
		else if (obj instanceof ERunType)
		{
			return obj == m_type; 
		}
		
		return false;
	}

	public RunType(String newType)
	{
		if (platformRhoSim.equals(newType))
		{
			m_type = ERunType.eRhoEmulator;
		}
		else if (platformSim.equals(newType))
		{
			m_type = ERunType.eEmulator;
		}
		else if (platformDevice.equals(newType))
		{
			m_type = ERunType.eDevice;
		}
	}
*/
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
		if (platformRhoSim.equals(newType))
		{
			return RunType.eRhoEmulator;
		}
		else if (platformSim.equals(newType))
		{
			return RunType.eEmulator;
		}
		else if (platformDevice.equals(newType))
		{
			return RunType.eDevice;
		}
		
		return eUnknow;
	}
//
//	public ERunType getType() 
//	{
//		return m_type;
//	}
}
