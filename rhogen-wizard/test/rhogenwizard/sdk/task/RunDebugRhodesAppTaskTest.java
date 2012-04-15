package rhogenwizard.sdk.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.ILogDevice;
import rhogenwizard.OSHelper;
import rhogenwizard.OSValidator;
import rhogenwizard.PlatformType;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.debugger.backend.DebugServer;
import rhogenwizard.debugger.backend.DebugState;
import rhogenwizard.debugger.backend.DebugVariableType;
import rhogenwizard.debugger.backend.IDebugCallback;
import rhogenwizard.sdk.facade.RhoTaskHolder;
import rhogenwizard.sdk.helper.TaskResultConverter;

public class RunDebugRhodesAppTaskTest
{
    private SynchronousQueue<String> m_eventQueue;
    private Semaphore m_semaphore;

    private static class DebugCallback implements IDebugCallback
    {
        private final SynchronousQueue<String> m_eventQueue;
        private final Semaphore m_semaphore;

        public DebugCallback(SynchronousQueue<String> eventQueue, Semaphore semaphore)
        {
            m_eventQueue = eventQueue;
            m_semaphore = semaphore;
        }

        @Override
        public void connected()
        {
            send("connected");
        }

        @Override
        public void stopped(DebugState state, String file, int line, String className,
                String method)
        {
            send("stopped [" + DebugState.getName(state) + "] [" + file + "] ["
                + line + "] [" + className + "] [" + method + "]");
        }

        @Override
        public void resumed()
        {
            send("resumed");
        }

        @Override
        public void evaluation(boolean valid, String code, String value)
        {
            send("evaluation [" + valid + "] [" + code + "] [" + value + "]");
        }

        @Override
        public void unknown(String cmd)
        {
            send("unknown [" + cmd + "]");
        }

        @Override
        public void exited()
        {
            send("exited");
        }

        @Override
        public void watch(DebugVariableType type, String variable, String value)
        {
            send("watch [" + DebugVariableType.getName(type) + "] [" + variable + "] ["
                + value + "]");
        }

        @Override
        public void watchBOL(DebugVariableType type)
        {
            send("watchBOL [" + DebugVariableType.getName(type) + "]");
        }

        @Override
        public void watchEOL(DebugVariableType type)
        {
            send("watchEOL [" + DebugVariableType.getName(type) + "]");
        }

        private void send(String event)
        {
            try
            {
                m_eventQueue.put(event);
                m_semaphore.acquire();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException("Can not send event. Impossible!", e);
            }
        }
    }

    private static ILogDevice nullLogDevice = new ILogDevice()
    {
        @Override
        public void log(String str)
        {
        }
    };
    private static final String workspaceFolder = new File(
            System.getProperty("java.io.tmpdir"), "junitworkfiles").getPath();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        ConsoleHelper.disableConsoles();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
        m_eventQueue = new SynchronousQueue<String>();
        m_semaphore = new Semaphore(0);

        OSHelper.deleteFolder(workspaceFolder);

        File newWsFodler = new File(workspaceFolder);
        newWsFodler.mkdir();
    }

    @After
    public void tearDown() throws Exception
    {
        OSHelper.deleteFolder(workspaceFolder);
    }

    @Test
    public void testRunDebugRhodesAppTask() throws Throwable
    {
        String appName = "app";
        String projectLocation = OSHelper.concat(workspaceFolder, appName).getPath();

        // create application
        {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(GenerateRhodesAppTask.appName, appName);
            params.put(GenerateRhodesAppTask.workDir, workspaceFolder);

            Map<String, ?> results =
                    RhoTaskHolder.getInstance().runTask(GenerateRhodesAppTask.class, params);

            assertEquals(0, TaskResultConverter.getResultIntCode(results));
        }

        // write new application.rb
        {
            String text[] =
            {
                /* 01 */"require 'rho/rhoapplication'",
                /* 02 */"class AppApplication < Rho::RhoApplication",
                /* 03 */"  def initialize",
                /* 04 */"    super",
                /* 05 */"    x = 0",
                /* 06 */"    x = x + 1",
                /* 07 */"  end",
                /* 08 */"end",
                /* 09 */""
            };
            String appRb = OSHelper.concat(projectLocation, "app", "application.rb").getPath();
            writeTextFile(appRb, join("\n", text));
        }

        // start debug server
        DebugCallback debugCallback = new DebugCallback(m_eventQueue, m_semaphore);
        final DebugServer debugServer = new DebugServer(debugCallback);
        final Throwable[] exception = new Throwable[1];
        Thread debugServerThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    debugServer.run();
                }
                catch (Throwable t)
                {
                    exception[0] = t;
                }
            }
        });
        debugServerThread.start();

        // run debug Rhodes application [android] [rhosimulator]
        {
            Map<String, Object> params = new HashMap<String, Object>();

            ILaunch launch = new Launch(null, ILaunchManager.DEBUG_MODE, null);

            params.put(RunDebugRhodesAppTask.workDir, projectLocation);
            params.put(RunDebugRhodesAppTask.appName, appName);
            params.put(RunDebugRhodesAppTask.platformType, PlatformType.eAndroid);
            params.put(RunDebugRhodesAppTask.reloadCode, false);
            params.put(RunDebugRhodesAppTask.launchObj, launch);
            params.put(RunDebugRhodesAppTask.traceFlag, false);

            Map<String, ?> results =
                    RhoTaskHolder.getInstance().runTask(RunDebugRhodesAppTask.class, params);
            assertEquals(TaskResultConverter.okCode,
                    TaskResultConverter.getResultIntCode(results));
        }

        suspend("connected");
        debugServer.debugBreakpoint("application.rb", 6);
        resume();

        pass("unknown [HOST=127.0.0.1]");
        pass("unknown [PORT=9000]");
        pass("unknown [DEBUG PATH=/private" + projectLocation + "/app/]");

        suspend("stopped [breakpoint] [application.rb] [6] [AppApplication] [initialize]");
        debugServer.debugEvaluate("x");
        resume();

        suspend("evaluation [true] [x] [0]");
        debugServer.debugTerminate();
        resume();

        suspend("exited");
        debugServer.shutdown();
        resume();

        debugServerThread.join();
        if (exception[0] != null)
        {
            throw exception[0];
        }
    }

    private void suspend(String s) throws InterruptedException
    {
        String event = m_eventQueue.poll(1, TimeUnit.MINUTES);
        if (event == null)
        {
            fail("timeout for \"" + s + "\"");
        }
        assertEquals(s, event);
    }

    private void resume()
    {
        m_semaphore.release();
    }

    private void pass(String s) throws InterruptedException
    {
        suspend(s);
        resume();
    }

    private static String join(String delimiter, String... text)
    {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String line : text)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                sb.append(delimiter);
            }
            sb.append(line);
        }
        return sb.toString();
    }

    private static String readTextFile(String filename) throws IOException
    {
        FileReader fr = new FileReader(filename);
        try
        {
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[16 * 1024];
            while (true)
            {
                int read = fr.read(buffer);
                if (read == -1)
                {
                    break;
                }
                sb.append(buffer, 0, read);
            }
            return sb.toString();
        }
        finally
        {
            fr.close();
        }
    }

    private static void writeTextFile(String filename, String text) throws IOException
    {
        FileWriter fw = new FileWriter(filename);
        try
        {
            fw.write(text);
        }
        finally
        {
            fw.close();
        }
    }

    private static String getProcessesListing() throws Exception
    {
        // TODO: add Windows and Linux processing
        if (!OSValidator.isMac())
        {
            return "";
        }

        List<String> cmdLine = Arrays.asList("ps", "ax");

        SysCommandExecutor executor = new SysCommandExecutor();
        executor.setOutputLogDevice(nullLogDevice);
        executor.setErrorLogDevice(nullLogDevice);
        executor.runCommand(cmdLine);

        return executor.getCommandOutput();
    }

    private static Set<Integer> getProcessesIds(String signature) throws Exception
    {
        Pattern pattern = Pattern.compile("^ *(\\d+).*");

        String listing = getProcessesListing();
        Set<Integer> ids = new HashSet<Integer>();
        for (String line : listing.split("\n"))
        {
            if (!line.contains(signature))
            {
                continue;
            }
            Matcher matcher = pattern.matcher(line);
            if (!matcher.matches())
            {
                continue;
            }
            ids.add(Integer.parseInt(matcher.group(1)));
        }
        return ids;
    }
}
