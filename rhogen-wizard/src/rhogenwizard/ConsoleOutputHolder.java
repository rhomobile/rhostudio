package rhogenwizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.ui.console.MessageConsoleStream;


class ConsoleOutputHolder implements ILogDevice
{
	List<String> outputData = new ArrayList<String>();

	@Override
	public void log(String str) 
	{
		outputData.add(str);
		str = null;
	}

	String findStringContain(String regex)
	{
		for (String str : outputData)
		{
			if (str.contains(regex))
			{
				return str;
			}	
		} 
		
		return null;
	}
	
	String getStringForIndex(int idx)
	{
		return outputData.get(idx);
	}
}