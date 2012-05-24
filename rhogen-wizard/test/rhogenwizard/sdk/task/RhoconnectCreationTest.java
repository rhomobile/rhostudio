package rhogenwizard.sdk.task;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.OSHelper;
import rhogenwizard.sdk.facade.RhoTaskHolder;
import rhogenwizard.sdk.helper.TaskResultConverter;
import rhogenwizard.sdk.task.GenerateRhoconnectAdapterTask;
import rhogenwizard.sdk.task.GenerateRhoconnectAppTask;
import junit.framework.TestCase;

public class RhoconnectCreationTest extends TestCase
{
    private static final String workspaceFolder = new File(
            System.getProperty("java.io.tmpdir"), "junitworkfiles").getPath();

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

        RakeTask task = new GenerateRhoconnectAppTask(workspaceFolder, appName);
        task.run();
        assertEquals(0, TaskResultConverter.getResultIntCode(task.getResult()));

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
            RakeTask task = new GenerateRhoconnectAppTask(workspaceFolder, appName);
            task.run();
            assertEquals(0, TaskResultConverter.getResultIntCode(task.getResult()));

            assertTrue(checkCreateRhoconnectFile(projectLocation));
        }

        // create adapter
        {
            RakeTask task = new GenerateRhoconnectAdapterTask(projectLocation, adapterName);
            task.run();
            assertEquals(0, TaskResultConverter.getResultIntCode(task.getResult()));

            assertTrue(OSHelper.concat(projectLocation, "sources", "adapter001.rb").isFile());
            assertTrue(OSHelper
                    .concat(projectLocation, "spec", "sources", "adapter001_spec.rb").isFile());
        }
    }
}
