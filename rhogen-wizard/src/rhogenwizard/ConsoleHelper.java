package rhogenwizard;

import java.io.OutputStream;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleHelper
{
	private static final String appConsoleName = "Rhomobile application console";
	private static final String buildConsoleName = "Rhomobile build console";
	
	private static MessageConsole msgConsole = null;
	private static MessageConsole appConsole = null;
	private static MessageConsoleStream appConsoleStream = null;
	private static MessageConsoleStream buildConsoleStream = null;
	
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
   
   public static MessageConsoleStream  getBuildConsoleStream()
   {
	   if (buildConsoleStream == null)
	   {
		   MessageConsole myConsole = findConsole(buildConsoleName);
		   buildConsoleStream = myConsole.newMessageStream();
	   }
	   
	   return buildConsoleStream;
   }

   public static MessageConsoleStream  getAppsConsoleStream()
   {
	   if (appConsoleStream == null)
	   {
		   MessageConsole myConsole = findConsole(appConsoleName);
		   appConsoleStream = myConsole.newMessageStream();
	   }
	   
	   return appConsoleStream;
   }

   public static void consoleAppPrint(String msg)
   {
	   showAppConsole();
	   getAppsConsoleStream().println(msg);
   }

   public static void consoleBuildPrint(String msg)
   {
	   getBuildConsoleStream().println(msg);
   }
   
   public static void showBuildConsole()
   {
	   ConsolePlugin   plugin = ConsolePlugin.getDefault();
	   IConsoleManager conMan = plugin.getConsoleManager();
	   
	   MessageConsole myConsole = findConsole(buildConsoleName);
	   conMan.showConsoleView(myConsole);
	   conMan.refresh(myConsole);
   }
   
   public static void showAppConsole()
   {
	   ConsolePlugin   plugin = ConsolePlugin.getDefault();
	   IConsoleManager conMan = plugin.getConsoleManager();
	   
	   MessageConsole myConsole = findConsole(appConsoleName);
	   conMan.showConsoleView(myConsole);
	   conMan.refresh(myConsole);
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