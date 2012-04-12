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

        Map<String, Object> params = new HashMap<String, Object>();

        params.put(GenerateRhoconnectAppTask.appName, appName);
        params.put(GenerateRhoconnectAppTask.workDir, workspaceFolder);

        Map<String, ?> results =
                RhoTaskHolder.getInstance().runTask(GenerateRhoconnectAppTask.class, params);

        assertEquals(TaskResultConverter.getResultIntCode(results), 0);

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
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(GenerateRhoconnectAppTask.appName, appName);
            params.put(GenerateRhoconnectAppTask.workDir, workspaceFolder);

            Map<String, ?> results =
                    RhoTaskHolder.getInstance()
                            .runTask(GenerateRhoconnectAppTask.class, params);

            assertEquals(TaskResultConverter.getResultIntCode(results), 0);

            assertTrue(checkCreateRhoconnectFile(projectLocation));
        }

        // create adapter
        {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(GenerateRhoconnectAdapterTask.sourceName, adapterName);
            params.put(GenerateRhoconnectAdapterTask.workDir, projectLocation);

            Map<String, ?> results =
                    RhoTaskHolder.getInstance().runTask(GenerateRhoconnectAdapterTask.class,
                            params);

            assertEquals(TaskResultConverter.getResultIntCode(results), 0);
            assertTrue(OSHelper.concat(projectLocation, "sources", "adapter001.rb").isFile());
            assertTrue(OSHelper
                    .concat(projectLocation, "spec", "sources", "adapter001_spec.rb").isFile());
        }
    }
}
