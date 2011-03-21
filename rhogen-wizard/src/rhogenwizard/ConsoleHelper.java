package rhogenwizard;

import java.io.OutputStream;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

class ConsoleHelper
{
   public static MessageConsole findConsole(String name) 
   {
		ConsolePlugin   plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[]      existing = conMan.getConsoles();
		
		for (int i = 0; i < existing.length; i++)
		{
			if (name.equals(existing[i].getName()))
			{
				return (MessageConsole) existing[i];
			}
		}
		
	    //no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[]{myConsole});
		
		return myConsole;
   }

   public static OutputStream getConsoleStream()
   {
	   MessageConsole myConsole = findConsole("Rhodes console");
	   MessageConsoleStream out = myConsole.newMessageStream();
	   return out;
   }
   
   public static MessageConsoleStream  getConsoleMsgStream()
   {
	   MessageConsole myConsole = findConsole("");
	   MessageConsoleStream out = myConsole.newMessageStream();
	   return out;
   }
}