package rhogenwizard;

import java.io.OutputStream;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleHelper
{
	private static final String appConsoleName = "Rhodes application console";
	private static final String buildConsoleName = "Rhodes build console";
	
	private static MessageConsole msgConsole = null;
	private static MessageConsole appConsole = null;
	
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
		if (msgConsole == null)
		{
			msgConsole = findConsole(buildConsoleName);
			appConsole = findConsole(appConsoleName);
		}
		
		MessageConsoleStream out = msgConsole.newMessageStream();
		
	   	return out;
	}
   
   public static MessageConsoleStream  getConsoleMsgStream()
   {
	   MessageConsole myConsole = findConsole(buildConsoleName);
	   MessageConsoleStream out = myConsole.newMessageStream();
	   return out;
   }

   public static MessageConsoleStream  getConsoleAppStream()
   {
	   MessageConsole myConsole = findConsole(appConsoleName);
	   MessageConsoleStream out = myConsole.newMessageStream();
	   return out;
   }

   public static void consoleAppPrint(String msg)
   {
	   getConsoleAppStream().println(msg);
   }

   public static void consolePrint(String msg)
   {
	   getConsoleMsgStream().println(msg);
   }
   
   public static void showAppConsole()
   {
	   ConsolePlugin   plugin = ConsolePlugin.getDefault();
	   IConsoleManager conMan = plugin.getConsoleManager();
	   
	   MessageConsole myConsole = findConsole(appConsoleName);
	   conMan.showConsoleView(myConsole);
   }
   
   public static void cleanAppConsole()
   {
	   MessageConsole myConsole = findConsole(appConsoleName);
	   myConsole.clearConsole();
   }

   public static void cleanBuildConsole()
   {
	   MessageConsole myConsole = findConsole(buildConsoleName);
	   myConsole.clearConsole();
   }
}