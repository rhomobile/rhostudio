package rhogenwizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
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
        void clear();
        void disable();
        Stream getStream();
        Stream getOutputStream();
        Stream getErrorStream();
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
                                          public void clear()
                                          {
                                          }

                                          @Override
                                          public void disable()
                                          {
                                          }

                                          @Override
                                          public Stream getStream()
                                          {
                                              return nullStream;
                                          }

                                          @Override
                                          public Stream getOutputStream()
                                          {
                                              return nullStream;
                                          }

                                          @Override
                                          public Stream getErrorStream()
                                          {
                                              return nullStream;
                                          }
                                      };

    private static class StreamImpl implements Stream
    {
        private boolean              m_enabled;
        private MessageConsoleStream m_stream;

        public StreamImpl(MessageConsole console, int swtColorId)
        {
            m_enabled = true;
            m_stream = console.newMessageStream();
            setColor(m_stream, swtColorId);
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
                m_stream.print(message);
            }
        }

        @Override
        public void println()
        {
            if (m_enabled)
            {
                m_stream.println();
            }
        }

        @Override
        public void println(String message)
        {
            if (m_enabled)
            {
                m_stream.println(message);
            }
        }

        private static void setColor(final MessageConsoleStream stream, final int swtColorId)
        {
            Display display1 = Display.getCurrent();
            if (display1 != null)
            {
                stream.setColor(display1.getSystemColor(swtColorId));
                return;
            }
            final Display display2 = Display.getDefault();
            if (display2 != null)
            {
                display2.asyncExec(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        stream.setColor(display2.getSystemColor(swtColorId));
                    }
                });
            }
        }
    }

    private static class ConsoleImpl implements Console
    {
        private final MessageConsole m_console;
        private final StreamImpl     m_stream;
        private final StreamImpl     m_outputStream;
        private final StreamImpl     m_errorStream;
        private boolean              m_enabled;

        public ConsoleImpl(String name)
        {
            m_console = findConsole(name);
            m_stream = new StreamImpl(m_console, SWT.COLOR_BLACK);
            m_outputStream = new StreamImpl(m_console, SWT.COLOR_DARK_BLUE);
            m_errorStream = new StreamImpl(m_console, SWT.COLOR_DARK_RED);

            m_enabled = true;
        }

        @Override
        public void clear()
        {
            if (m_enabled)
            {
                m_console.clearConsole();
            }
        }

        @Override
        public void show()
        {
            if (m_enabled)
            {
                IConsoleManager conMan = ConsolePlugin.getDefault().getConsoleManager();

                conMan.showConsoleView(m_console);
                conMan.refresh(m_console);
            }
        }

        @Override
        public Stream getStream()
        {
            return m_stream;
        }

        @Override
        public Stream getOutputStream()
        {
            return m_outputStream;
        }

        @Override
        public Stream getErrorStream()
        {
            return m_errorStream;
        }

        public void disable()
        {
            m_enabled = false;
            m_stream.disable();
            m_outputStream.disable();
            m_errorStream.disable();
        }

        private static MessageConsole findConsole(String name)
        {
            ConsolePlugin plugin = ConsolePlugin.getDefault();
            
            if (plugin == null)
            	return null;
            
            IConsoleManager conMan = plugin.getConsoleManager();
            
            if (conMan == null)
            	return null;
            
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

    private static Console appConsole   = null;
    private static Console buildConsole = null;

    public static void initialize()
    {
        if (appConsole == null)
        {
            appConsole = new ConsoleImpl("Rhomobile application console");
        }
        if (buildConsole == null)
        {
            buildConsole = new ConsoleImpl("Rhomobile build console");
        }
    }

    public static void setupNullConsoles()
    {
    	appConsole   = nullConsole;
    	buildConsole = nullConsole;
    }
    
    public static void disableConsoles()
    {
        initialize();
        appConsole.disable();
        buildConsole.disable();
    }

    public static Console getAppConsole()
    {
        initialize();
        return appConsole;
    }

    public static Console getBuildConsole()
    {
        initialize();
        return buildConsole;
    }
}
