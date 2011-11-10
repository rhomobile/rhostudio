package rhogenwizard.sdk.helper;

import org.eclipse.ui.console.MessageConsoleStream;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.ILogDevice;

public class ConsoleAdapter implements ILogDevice 
{
	MessageConsoleStream m_consoleStream = ConsoleHelper.getConsoleMsgStream();

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
