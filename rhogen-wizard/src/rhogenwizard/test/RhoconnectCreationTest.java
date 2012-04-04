package rhogenwizard.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

    boolean checkCreateRhoconnectFile(String path)
    {
        String pathToBuildYml = path + File.separator + "config.ru";
        File f = new File(pathToBuildYml);

        return f.isFile();
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
    public void testCreateRhoconnectApp()
    {
        String appName = "test003";

        Map<String, Object> params = new HashMap<String, Object>();

        params.put(GenerateRhoconnectAppTask.appName, appName);
        params.put(GenerateRhoconnectAppTask.workDir, workspaceFolder);

        Map results =
                RhoTaskHolder.getInstance().runTask(GenerateRhoconnectAppTask.class, params);

        try
        {
            assertEquals(TaskResultConverter.getResultIntCode(results), 0);
        }
        catch (Exception e)
        {
            fail("fail on check result [test3]");
        }

        assertEquals(checkCreateRhoconnectFile(workspaceFolder + File.separator + appName),
                true);
    }

    @Test
    public void testCreateRhoconnectSrcAdapter()
    {
        String appName = "test004";
        String adapterName = "adapter001";
        String projectLocation = workspaceFolder + File.separator + appName;

        Map<String, Object> params = new HashMap<String, Object>();

        params.put(GenerateRhoconnectAppTask.appName, appName);
        params.put(GenerateRhoconnectAppTask.workDir, workspaceFolder);

        Map results =
                RhoTaskHolder.getInstance().runTask(GenerateRhoconnectAppTask.class, params);

        try
        {
            assertEquals(TaskResultConverter.getResultIntCode(results), 0);
        }
        catch (Exception e)
        {
            fail("fail on check result [test4]");
        }

        assertEquals(checkCreateRhoconnectFile(projectLocation), true);

        params.clear();
        params.put(GenerateRhoconnectAdapterTask.sourceName, adapterName);
        params.put(GenerateRhoconnectAdapterTask.workDir, projectLocation);

        results =
                RhoTaskHolder.getInstance()
                        .runTask(GenerateRhoconnectAdapterTask.class, params);

        try
        {
            assertEquals(TaskResultConverter.getResultIntCode(results), 0);
        }
        catch (Exception e)
        {
            fail("fail on check result [test4]");
        }
    }
}
