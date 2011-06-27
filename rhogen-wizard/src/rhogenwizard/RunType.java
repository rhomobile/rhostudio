package rhogenwizard;

public class RunType 
{
	/* platforms types */
	public static final String platformRhoSim = "RhoSimulator";
	public static final String platformSim    = "Simulator";
	public static final String platformDevice = "Device";

	public enum ERunType
	{
		eDevice,
		eEmulator,
		eRhoEmulator
	};
	
	private ERunType m_type = ERunType.eRhoEmulator;
	
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

	@Override
	public String toString()
	{
		switch(m_type)
		{
		case eDevice:
			return platformDevice;
		case eEmulator:
			return platformSim;
		case eRhoEmulator:
			return platformRhoSim;
		}
		
		return "";
	}

	public ERunType getType() 
	{
		return m_type;
	}
}
