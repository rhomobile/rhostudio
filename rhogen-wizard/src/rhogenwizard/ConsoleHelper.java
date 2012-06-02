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
        void print(String message);

        void println();

        void println(String message);
    }

    public interface Console
    {
        void show();

        Stream getStream();
    }

    public static Stream  nullStream  = new Stream()
                                      {
                                          @Override
                                          public void println(String message)
                                          {
                                          }

                                          @Override
                                          public void println()
                                          {
                                          }

                                          @Override
                                          public void print(String message)
                                          {
                                          }
                                      };

    public static Console nullConsole = new Console()
                                      {
                                          @Override
                                          public void show()
                                          {
                                          }

                                          @Override
                                          public Stream getStream()
                                          {
                                              return nullStream;
                                          }
                                      };

    private static class LazyStream implements Stream
    {
        private final String         m_console;
        private boolean              m_enabled;
        private MessageConsoleStream m_stream;

        public LazyStream(String console)
        {
            m_console = console;
            m_enabled = true;
            m_stream = null;
        }

        public void disable()
        {
            m_enabled = false;
        }

        @Override
        public void print(String message)
        {
            if (m_enabled)
            {
                getStream().print(message);
            }
        }

        @Override
        public void println()
        {
            if (m_enabled)
            {
                getStream().println();
            }
        }

        @Override
        public void println(String message)
        {
            if (m_enabled)
            {
                getStream().println(message);
            }
        }

        private MessageConsoleStream getStream()
        {
            if (m_stream == null)
            {
                m_stream = findConsole(m_console).newMessageStream();
            }
            return m_stream;
        }
    }

    private static class LazyConsole implements Console
    {
        private final String     m_name;
        private boolean          m_enabled;
        private final LazyStream m_stream;
        private MessageConsole   m_console;

        public LazyConsole(String name)
        {
            m_name = name;
            m_enabled = true;
            m_stream = new LazyStream(name);
            m_console = null;
        }

        @Override
        public void show()
        {
            if (m_enabled)
            {
                ConsolePlugin plugin = ConsolePlugin.getDefault();
                IConsoleManager conMan = plugin.getConsoleManager();

                if (m_console == null)
                {
                    m_console = findConsole(m_name);
                }
                conMan.showConsoleView(m_console);
                conMan.refresh(m_console);
            }
        }

        @Override
        public Stream getStream()
        {
            return m_stream;
        }

        public void disable()
        {
            m_enabled = false;
            m_stream.disable();
        }
    }

    private static final String      appConsoleName   = "Rhomobile application console";
    private static final String      buildConsoleName = "Rhomobile build console";

    private static final LazyConsole appConsole       = new LazyConsole(appConsoleName);
    private static final LazyConsole buildConsole     = new LazyConsole(buildConsoleName);

    public static void disableConsoles()
    {
        appConsole.disable();
        buildConsole.disable();
    }

    public static Console getBuildConsole()
    {
        return buildConsole;
    }

    public static Console getAppsConsole()
    {
        return appConsole;
    }

    public static Stream getBuildConsoleStream()
    {
        return buildConsole.getStream();
    }

    public static Stream getAppsConsoleStream()
    {
        return appConsole.getStream();
    }

    public static void consoleAppPrintln(String msg)
    {
        getAppsConsoleStream().println(msg);
    }

    public static void consoleBuildPrintln(String msg)
    {
        getBuildConsoleStream().println(msg);
    }

    public static void showBuildConsole()
    {
        buildConsole.show();
    }

    public static void showAppConsole()
    {
        appConsole.show();
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
