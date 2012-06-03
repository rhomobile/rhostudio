package rhogenwizard.sdk.helper;

import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;

import rhogenwizard.ConsoleHelper;


class OutputStreamListener implements IStreamListener
{
    ConsoleHelper.Stream m_consoleStream = ConsoleHelper.getBuildConsole().getStream();

	@Override
	public void streamAppended(String text, IStreamMonitor monitor)
	{
		if (null != m_consoleStream)
		{
			m_consoleStream.println(text);
		}
	}	
}

class ErrorStreamListener implements IStreamListener
{
    ConsoleHelper.Stream m_consoleStream = ConsoleHelper.getBuildConsole().getStream();

	@Override
	public void streamAppended(String text, IStreamMonitor monitor)
	{
		if (null != m_consoleStream)
		{
			m_consoleStream.println("Error - " + text);
		}
	}	
}

/**
 * @author anton vishenvskiy
 *
 */
public class DebugConsoleAdapter
{	
	public DebugConsoleAdapter(IProcess p)
	{
		if (p != null)
		{
			p.getStreamsProxy().getErrorStreamMonitor().addListener(new ErrorStreamListener());
			p.getStreamsProxy().getOutputStreamMonitor().addListener(new OutputStreamListener());
		}
	}
}
