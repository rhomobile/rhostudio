package rhogenwizard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OSHelper extends OSValidator
{
    private static ILogDevice nullLogDevice = new ILogDevice()
    {
        @Override
        public void log(String str)
        {
        }
    };

    public static void killScriptProcess(String processName) throws Exception
    {
        killProcess(processName, processName + ".bat");
    }

    public static void killProcess(int pid) throws InterruptedException
    {
        List<String> cmdLine;
        if (OSValidator.OSType.WINDOWS == OSValidator.detect())
        {
            cmdLine = Arrays.asList("taskkill.exe", "/F", "/PID", Integer.toString(pid));
        }
        else
        {
            cmdLine = Arrays.asList("kill", "-9", Integer.toString(pid));
        }
        try
        {
            new SysCommandExecutor().runCommand(SysCommandExecutor.CRT, cmdLine);
        }
        catch (IOException e)
        {
            throw new RuntimeException("impossible", e);
        }
    }

    public static void killProcesses(Iterable<Integer> pids) throws InterruptedException
    {
        for (int pid : pids)
        {
            killProcess(pid);
        }
    }

    public static void killProcess(String processName) throws Exception
    {
        killProcess(processName, processName + ".exe");
    }

    public static void killProcess(String unixName, String wndName) throws Exception
    {
        List<String> cmdLine = new ArrayList<String>();

        if (OSValidator.OSType.WINDOWS == OSValidator.detect())
        {
            cmdLine.add("taskkill.exe");
            cmdLine.add("/F");
            cmdLine.add("/IM");
            cmdLine.add(wndName);
        }
        else
        {
            cmdLine.add("killall");
            cmdLine.add("-9");
            cmdLine.add(unixName);
        }

        SysCommandExecutor executor = new SysCommandExecutor();
        executor.runCommand(SysCommandExecutor.CRT, cmdLine);
    }

    public static void deleteFolder(String pathToRootFolder)
    {
        File rootFolder = new File(pathToRootFolder);

        deleteFolder(rootFolder);
    }

    public static void deleteFolder(File rootFolder)
    {
        if (!rootFolder.isDirectory())
        {
            rootFolder.delete();
            return;
        }

        File[] containFiles = rootFolder.listFiles();

        for (File currFile : containFiles)
        {
            deleteFolder(currFile);
        }

        rootFolder.delete();
    }

    public static File concat(String... paths)
    {
        // under Windows concatenation starting with [new File("")]
        // leads to path starting with [\]

        File file = new File((paths.length <= 0) ? "" : paths[0]);
        for (int i = 1; i < paths.length; i++)
        {
            file = new File(file, paths[i]);
        }
        return file;
    }

    public static Set<Integer> getProcessesIds(String commandLineFragment)
            throws InterruptedException
    {
        Pattern pattern =
                Pattern.compile((OSValidator.isWindows()) ? "^.* (\\d+) *$" : "^ *(\\d+).*$");

        String listing = getProcessesListing();
        Set<Integer> ids = new HashSet<Integer>();
        for (String line : listing.split("[\n\r]+"))
        {
            if (!line.contains(commandLineFragment))
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

    private static String getProcessesListing() throws InterruptedException
    {
        String cl =
                (OSValidator.isWindows()) ? "wmic path win32_process get Commandline,Processid"
                        : "ps ax";
        List<String> cmdLine = Arrays.asList(cl.split(" "));

        SysCommandExecutor executor = new SysCommandExecutor();
        executor.setOutputLogDevice(nullLogDevice);
        executor.setErrorLogDevice(nullLogDevice);
        try
        {
            executor.runCommand(SysCommandExecutor.CRT, cmdLine);
        }
        catch (IOException e)
        {
            Activator.logErrorAndThrow("can not get process list", e);
        }
        return executor.getCommandOutput();
    }
}
