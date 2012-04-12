package rhogenwizard.sdk.task;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import rhogenwizard.RunType;
import rhogenwizard.SysCommandExecutor;
import rhogenwizard.sdk.facade.RhoTaskHolder;
import rhogenwizard.sdk.helper.TaskResultConverter;

public class RunReleaseRhodesAppTaskTest
{
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
    public void testRunReleaseRhodesAppTask() throws Exception
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

        // run release Rhodes application [iphone] [simulator]
        // TODO: remove "if" when "rake run:iphone" will be fixed
        if (false)
        {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(RunReleaseRhodesAppTask.workDir, projectLocation);
            params.put(RunReleaseRhodesAppTask.platformType, PlatformType.eIPhone);
            params.put(RunReleaseRhodesAppTask.runType, RunType.eEmulator);
            params.put(RunReleaseRhodesAppTask.reloadCode, false);
            params.put(RunReleaseRhodesAppTask.traceFlag, false);

            String signature = "-e logcat >> \"/private" + projectLocation + "/rholog.txt\""; 

            Set<Integer> before = getProcessesIds(signature);

            Map<String, ?> results =
                    RhoTaskHolder.getInstance().runTask(RunReleaseRhodesAppTask.class, params);
            assertEquals(0, TaskResultConverter.getResultIntCode(results));

            Set<Integer> after = getProcessesIds(signature);

            Set<Integer> diff = new HashSet<Integer>(after);
            diff.removeAll(before);

            assertEquals(1, diff.size());

            for (int pid : diff)
            {
                OSHelper.killProcess(pid);
            }
        }

        // run release Rhodes application [android] [simulator]
        {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(RunReleaseRhodesAppTask.workDir, projectLocation);
            params.put(RunReleaseRhodesAppTask.platformType, PlatformType.eAndroid);
            params.put(RunReleaseRhodesAppTask.runType, RunType.eEmulator);
            params.put(RunReleaseRhodesAppTask.reloadCode, false);
            params.put(RunReleaseRhodesAppTask.traceFlag, false);

            String signature = "-e logcat >> \"/private" + projectLocation + "/rholog.txt\""; 

            Set<Integer> before = getProcessesIds(signature);

            Map<String, ?> results =
                    RhoTaskHolder.getInstance().runTask(RunReleaseRhodesAppTask.class, params);
            assertEquals(0, TaskResultConverter.getResultIntCode(results));

            Set<Integer> after = getProcessesIds(signature);

            Set<Integer> diff = new HashSet<Integer>(after);
            diff.removeAll(before);

            assertEquals(1, diff.size());

            for (int pid : diff)
            {
                OSHelper.killProcess(pid);
            }
        }

        // run release Rhodes application [*] [rhosimulator]
        for (PlatformType platformType : PlatformType.values())
        {
            if (platformType == PlatformType.eRsync || platformType == PlatformType.eUnknown)
            {
                continue;
            }

            Map<String, Object> params = new HashMap<String, Object>();

            params.put(RunReleaseRhodesAppTask.workDir, projectLocation);
            params.put(RunReleaseRhodesAppTask.platformType, platformType);
            params.put(RunReleaseRhodesAppTask.runType, RunType.eRhoEmulator);
            params.put(RunReleaseRhodesAppTask.reloadCode, false);
            params.put(RunReleaseRhodesAppTask.traceFlag, false);

            String signature = "RhoSimulator -approot=/private" + projectLocation;

            Set<Integer> before = getProcessesIds(signature);

            Map<String, ?> results =
                    RhoTaskHolder.getInstance().runTask(RunReleaseRhodesAppTask.class, params);
            assertEquals(0, TaskResultConverter.getResultIntCode(results));

            Set<Integer> after = getProcessesIds(signature);

            Set<Integer> diff = new HashSet<Integer>(after);
            diff.removeAll(before);

            assertEquals(1, diff.size());

            for (int pid : diff)
            {
                OSHelper.killProcess(pid);
            }
        }

//        // run release Rhodes application
//        {
//            Map<String, Object> params = new HashMap<String, Object>();
//
//            for (PlatformType platformType : PlatformType.values())
//            {
//                for (RunType runType : RunType.values())
//                {
//                    params.put(RunReleaseRhodesAppTask.workDir, projectLocation);
//                    params.put(RunReleaseRhodesAppTask.platformType, platformType);
//                    params.put(RunReleaseRhodesAppTask.runType, runType);
//                    params.put(RunReleaseRhodesAppTask.reloadCode, false);
//                    params.put(RunReleaseRhodesAppTask.traceFlag, false);
//
//                    Map<String, ?> results =
//                            RhoTaskHolder.getInstance().runTask(RunReleaseRhodesAppTask.class,
//                                    params);
//                    System.out.println("running [" + platformType + "] [" + runType + "] "
//                            + TaskResultConverter.getResultIntCode(results));
//                }
//            }
//        }
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
