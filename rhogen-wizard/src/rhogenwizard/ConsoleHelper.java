package rhogenwizard;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleHelper
{
    public interface Stream
    {
        void println(String s);
    }

    private static class LazyStream implements Stream
    {
        private final String m_console;
        private boolean m_enabled;
        private MessageConsoleStream m_stream;

        public LazyStream(String console)
        {
            m_console = console;
            m_enabled = true;
            m_stream = null;
        }

        public void initialize()
        {
            if (m_stream == null)
            {
                m_stream = findConsole(m_console).newMessageStream();
            }
        }

        public void disable()
        {
            m_enabled = false;
        }

        @Override
        public void println(String s)
        {
            if (m_enabled)
            {
                m_stream.println(s);
            }
        }
    }

    private static final String appConsoleName = "Rhomobile application console";
    private static final String buildConsoleName = "Rhomobile build console";

    private static LazyStream appConsoleStream = new LazyStream(appConsoleName);
    private static LazyStream buildConsoleStream = new LazyStream(buildConsoleName);

    public static void disableConsoles()
    {
        appConsoleStream.disable();
        buildConsoleStream.disable();
    }

    public static Stream getBuildConsoleStream()
    {
        buildConsoleStream.initialize();
        return buildConsoleStream;
    }

    public static Stream getAppsConsoleStream()
    {
        appConsoleStream.initialize();
        return appConsoleStream;
    }

    public static void consoleAppPrint(String msg)
    {
        getAppsConsoleStream().println(msg);
    }

    public static void consoleBuildPrint(String msg)
    {
        getBuildConsoleStream().println(msg);
    }

    public static void showBuildConsole()
    {
        ConsolePlugin plugin = ConsolePlugin.getDefault();
        IConsoleManager conMan = plugin.getConsoleManager();

        MessageConsole myConsole = findConsole(buildConsoleName);
        conMan.showConsoleView(myConsole);
        conMan.refresh(myConsole);
    }

    public static void showAppConsole()
    {
        ConsolePlugin plugin = ConsolePlugin.getDefault();
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

    private static MessageConsole findConsole(String name)
    {
        ConsolePlugin plugin = ConsolePlugin.getDefault();
        IConsoleManager conMan = plugin.getConsoleManager();
        IConsole[] existing = conMan.getConsoles();

        for (int i = 0; i < existing.length; i++)
        {
            if (name.equals(existing[i].getName()))
            {
                return (MessageConsole) existing[i];
            }
        }

        // no console found, so create a new one
        MessageConsole myConsole = new MessageConsole(name, null);
        conMan.addConsoles(new IConsole[] { myConsole });

        return myConsole;
    }
}
