package rhogenwizard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SysCommandExecutor
{
    // use it to run executables with "standard" command line parser
    public static Decorator      CRT                  = new Decorator(1)
                                                      {
                                                          public String decorate(String arg)
                                                          {
                                                              return decorateCrt(arg);
                                                          }
                                                      };

    // Ruby on Windows uses special command line parser
    public static Decorator      RUBY                 = new Decorator(1)
                                                      {
                                                          public String decorate(String arg)
                                                          {
                                                              return decorateRuby(arg);
                                                          }
                                                      };

    // Some Ruby commands on Windows are batches. This decorator is for rake,
    // gem and so on.
    public static Decorator      RUBY_BAT             = new Decorator(2)
                                                      {
                                                          public String decorate(String arg)
                                                          {
                                                              return decorateRuby(arg);
                                                          }
                                                      };

    private ILogDevice           m_ouputLogDevice     = null;
    private ILogDevice           m_errorLogDevice     = null;
    private String               m_workingDirectory   = null;
    private List<EnvironmentVar> m_environmentVarList = null;

    private StringBuffer         m_cmdOutput          = null;
    private StringBuffer         m_cmdError           = null;
    private AsyncStreamReader    m_cmdOutputThread    = null;
    private AsyncStreamReader    m_cmdErrorThread     = null;

    public void setOutputLogDevice(ILogDevice logDevice)
    {
        m_ouputLogDevice = logDevice;
    }

    public void setErrorLogDevice(ILogDevice logDevice)
    {
        m_errorLogDevice = logDevice;
    }

    public void setWorkingDirectory(String workingDirectory)
    {
        m_workingDirectory = workingDirectory;
    }

    public void setEnvironmentVar(String name, String value)
    {
        if (m_environmentVarList == null)
            m_environmentVarList = new ArrayList<EnvironmentVar>();

        m_environmentVarList.add(new EnvironmentVar(name, value));
    }

    public String getCommandOutput()
    {
        return m_cmdOutput.toString();
    }

    public String getCommandError()
    {
        return m_cmdError.toString();
    }

    public Process startCommand(Decorator decorator, List<String> commandLine) throws IOException
    {
        if (m_cmdOutput != null)
        {
            m_cmdOutput.delete(0, m_cmdOutput.length());
        }

        /* run command */
        Process process = runCommandHelper(Arrays.asList(decorateCommandLine(decorator,
            commandLine.toArray(new String[0]))));

        /* close process input stream (required for WMIC on Win32) */
        process.getOutputStream().close();

        /* start output and error read threads */
        startOutputAndErrorReadThreads(process.getInputStream(), process.getErrorStream());

        return process;
    }

    public int runCommand(Decorator decorator, List<String> commandLine) throws IOException,
        InterruptedException
    {
        Process process = startCommand(decorator, commandLine);

        try
        {
            return process.waitFor();
        }
        finally
        {
            /* notify output and error read threads to stop reading */
            notifyOutputAndErrorReadThreadsToStopReading();
        }
    }

    private Process runCommandHelper(List<String> commandLine) throws IOException
    {
        ProcessBuilder pb = new ProcessBuilder(commandLine);

        if (m_workingDirectory != null)
        {
            pb.directory(new File(m_workingDirectory));
        }

        if (m_environmentVarList != null && !m_environmentVarList.isEmpty())
        {
            for (EnvironmentVar envVar : m_environmentVarList)
            {
                pb.environment().put(envVar.m_envName, envVar.m_envValue);
            }
        }

        return pb.start();
    }

    private void startOutputAndErrorReadThreads(InputStream processOut, InputStream processErr)
    {
        m_cmdOutput = new StringBuffer();
        m_cmdOutputThread = new AsyncStreamReader(false, processOut, m_cmdOutput, m_ouputLogDevice, "OUTPUT");
        m_cmdOutputThread.start();

        m_cmdError = new StringBuffer();
        m_cmdErrorThread = new AsyncStreamReader(false, processErr, m_cmdError, m_errorLogDevice, "ERROR");
        m_cmdErrorThread.start();
    }

    private void notifyOutputAndErrorReadThreadsToStopReading() throws InterruptedException
    {
        m_cmdOutputThread.join(1000);

        if (m_cmdOutputThread.isAlive())
        {
            m_cmdOutputThread.stopReading();
        }

        m_cmdErrorThread.join(1000);

        if (m_cmdErrorThread.isAlive())
        {
            m_cmdErrorThread.stopReading();
        }
    }

    private static String[] decorateCommandLine(Decorator decorator, String... args)
    {
        if (!OSHelper.isWindows())
        {
            return args;
        }

        // Read this to understand command line decoration in Win32:
        // http://blogs.msdn.com/b/twistylittlepassagesallalike/archive/2011/04/23/everyone-quotes-arguments-the-wrong-way.aspx

        // In general we have to escape command line for command line parser (1)
        // and
        // then we have to escape command line for command interpreter (2).

        // 1

        // Merge arguments into command line.
        // Unfortunately there is no single standard way to escape command line
        // arguments in Win32.
        // Ruby parser is different from Win32 CommandLineToArgvW. We use
        // decorator to implement details.
        String cl = mergeArgs(decorator, args);

        // 2

        // Some characters (metacharacters) have special meaning for command
        // interpreter.
        // We have to escape them with '^' character.
        for (int i = 0; i < decorator.metaCount; i++)
        {
            cl = cl.replaceAll("[()%!^\"<>&|]", "^$0");
        }

        // If argument contains space or tab then ProcessBuilder surrounds it
        // with quotes.
        // Otherwise we have to add quotes yourself.
        // Quotes will be removed by command interpreter. See help for cmd /s
        // option.
        if (!containsAny(cl, " \t"))
        {
            cl = '"' + cl + '"';
        }

        return new String[] { "cmd", "/s", "/c", cl };
    }

    private static String mergeArgs(Decorator decorator, String[] args)
    {
        StringBuilder sb = new StringBuilder();

        boolean first = true;

        for (String arg : args)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                sb.append(' ');
            }

            sb.append(decorator.decorate(arg));
        }

        return sb.toString();
    }

    public static abstract class Decorator
    {
        // See MSDN "Parsing C++ Command-Line Arguments" article.
        protected static String decorateCrt(String arg)
        {
            String decor = (containsAny(arg, " \t")) ? "\"" : "";
            return decorate(decor, "\"", arg);
        }

        // See Ruby source code: rb_w32_cmdvector function in win32.c file.
        protected static String decorateRuby(String arg)
        {
            String shortest = decorate("\"", "'\"", arg);

            if (!containsAny(arg, " \t\n"))
            {
                String candidate = decorate("", "'\"", arg);

                if (candidate.length() <= shortest.length())
                {
                    shortest = candidate;
                }
            }

            if (!arg.contains("'"))
            {
                String candidate = decorate("'", "", arg);
                if (candidate.length() < shortest.length())
                {
                    shortest = candidate;
                }
            }

            return shortest;
        }

        public final int metaCount;

        private Decorator(int metaCount)
        {
            this.metaCount = metaCount;
        }

        public abstract String decorate(String arg);

        private static String decorate(String decor, String quotes, String text)
        {
            assert decor.length() <= 1;

            StringBuilder sb = new StringBuilder();

            sb.append(decor);
            Escaper e = new Escaper(sb, quotes);

            for (int i = 0; i < text.length(); i++)
            {
                e.append(text.charAt(i));
            }

            e.flush(decor.length() == 1 && quotes.contains(decor));
            sb.append(decor);

            return sb.toString();
        }

    }

    private static class Escaper
    {
        private final StringBuilder m_sb;
        private final String        m_quotes;
        private int                 m_nBackSlashes = 0;

        public Escaper(StringBuilder sb, String quotes)
        {
            m_sb = sb;
            m_quotes = quotes;
        }

        public void append(char c)
        {
            if (c == '\n')
            {
                throw new IllegalArgumentException();
            }
            else if (c == '\\')
            {
                ++m_nBackSlashes;
            }
            else if (m_quotes.indexOf(c) != -1)
            {
                flush(true);
                m_sb.append("\\");
                m_sb.append(c);
            }
            else
            {
                flush(false);
                m_sb.append(c);
            }
        }

        public void flush(boolean withQuote)
        {
            int n = (withQuote) ? 2 * m_nBackSlashes : m_nBackSlashes;

            for (int i = 0; i < n; i++)
            {
                m_sb.append('\\');
            }

            m_nBackSlashes = 0;
        }
    }

    private static boolean containsAny(String s, CharSequence chars)
    {
        for (int i = 0; i < chars.length(); i++)
        {
            if (s.indexOf(chars.charAt(i)) != -1)
            {
                return true;
            }
        }

        return false;
    }
}

class EnvironmentVar
{
    public String m_envName  = null;
    public String m_envValue = null;

    public EnvironmentVar(String name, String value)
    {
        m_envName = name;
        m_envValue = value;
    }
}
