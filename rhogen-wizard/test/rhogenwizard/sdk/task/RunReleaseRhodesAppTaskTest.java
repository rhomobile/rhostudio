package rhogenwizard.sdk.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.OSHelper;
import rhogenwizard.OSValidator;
import rhogenwizard.PlatformType;
import rhogenwizard.ProcessListViewer;
import rhogenwizard.RunType;

public class RunReleaseRhodesAppTaskTest
{
    private static final String workspaceFolder = new File(System.getProperty("java.io.tmpdir"),
                                                    "junitworkfiles").getPath();

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
            RunTask task = new GenerateRhodesAppTask(workspaceFolder, appName);
            task.run();
            assertTrue(task.isOk());

        }

        // run release Rhodes application [iphone] [simulator]
        // TODO: remove "if" when "rake run:iphone" will be fixed
        if (false)
        {
            ProcessListViewer plv = new ProcessListViewer("-e logcat >> \"/private" + projectLocation
                + "/rholog.txt\"");

            RunTask task = new RunReleaseRhodesAppTask(projectLocation, PlatformType.eIPhone,
                RunType.eEmulator, false, false);
            task.run();
            assertTrue(task.isOk());

            Set<Integer> diff = plv.getNewProcesses();
            assertEquals(1, diff.size());
            OSHelper.killProcesses(diff);
        }

        // run release Rhodes application [android] [simulator]
        {
            String signature = "-e logcat >> \""
                + unixSlashes(prependPrivate(OSHelper.concat(projectLocation, "rholog.txt").getPath()))
                + "\"";

            ProcessListViewer plv = new ProcessListViewer(signature);

            RunTask task = new RunReleaseRhodesAppTask(projectLocation, PlatformType.eAndroid,
                RunType.eEmulator, false, false);
            task.run();
            assertTrue(task.isOk());

            Set<Integer> diff = plv.getNewProcesses();
            assertEquals(1, diff.size());
            OSHelper.killProcesses(diff);
        }

        // run release Rhodes application [*] [rhosimulator]
        for (PlatformType platformType : PlatformType.values())
        {
            if (platformType == PlatformType.eRsync || platformType == PlatformType.eUnknown)
            {
                continue;
            }

            String signature = (OSValidator.isWindows()) ? "rhosimulator.exe -approot=\'"
                + unixSlashes(projectLocation) + "\'" : "RhoSimulator -approot=/private" + projectLocation;

            ProcessListViewer plv = new ProcessListViewer(signature);

            RunTask task = new RunReleaseRhodesAppTask(projectLocation, platformType, RunType.eRhoEmulator,
                false, false);
            task.run();
            assertTrue("for " + platformType, task.isOk());

            Set<Integer> diff = plv.getNewProcesses();
            assertEquals("for " + platformType, 1, diff.size());
            OSHelper.killProcesses(diff);
        }
    }

    private static String prependPrivate(String path)
    {
        return ((OSValidator.isWindows()) ? "" : "/private") + path;
    }

    private static String unixSlashes(String path)
    {
        return path.replace('\\', '/');
    }
}
