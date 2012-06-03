package rhogenwizard.sdk.helper;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.ILogDevice;

public class ConsoleAppAdapter implements ILogDevice 
{
    ConsoleHelper.Stream m_consoleStream = ConsoleHelper.getAppConsole().getStream();

	@Override
	public void log(String str) 
	{
		if (null != m_consoleStream)
		{
			m_consoleStream.println(prepareString(str));
		}
	}
	
	private String prepareString(String message)
	{
		message = message.replaceAll("\\p{Cntrl}", " ");  		
		return message;
	}
}