package rhogenwizard.sdk.task;

import java.io.File;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.OSHelper;

public class RhoconnectCreationTest extends TestCase
{
    private static final String workspaceFolder = new File(System.getProperty("java.io.tmpdir"),
                                                    "junitworkfiles").getPath();

    private boolean checkCreateRhoconnectFile(String path)
    {
        return OSHelper.concat(path, "config.ru").isFile();
    }

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
    public void testCreateRhoconnectApp() throws Exception
    {
        String appName = "test003";

        RunTask task = new GenerateRhoconnectAppTask(workspaceFolder, appName);
        task.run();
        assertTrue(task.isOk());

        assertTrue(checkCreateRhoconnectFile(workspaceFolder + File.separator + appName));
    }

    @Test
    public void testCreateRhoconnectSrcAdapter() throws Exception
    {
        String appName = "test004";
        String adapterName = "adapter001";
        String projectLocation = workspaceFolder + File.separator + appName;

        // create application
        {
            RunTask task = new GenerateRhoconnectAppTask(workspaceFolder, appName);
            task.run();
            assertTrue(task.isOk());

            assertTrue(checkCreateRhoconnectFile(projectLocation));
        }

        // create adapter
        {
            RunTask task = new GenerateRhoconnectAdapterTask(projectLocation, adapterName);
            task.run();
            assertTrue(task.isOk());

            assertTrue(OSHelper.concat(projectLocation, "sources", "adapter001.rb").isFile());
            assertTrue(OSHelper.concat(projectLocation, "spec", "sources", "adapter001_spec.rb").isFile());
        }
    }
}
