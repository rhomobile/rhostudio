package rhogenwizard.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rhogenwizard.OSHelper;
import rhogenwizard.sdk.facade.RhoTaskHolder;
import rhogenwizard.sdk.helper.TaskResultConverter;
import rhogenwizard.sdk.task.GenerateRhodesAppTask;
import rhogenwizard.sdk.task.GenerateRhodesExtensionTask;
import rhogenwizard.sdk.task.GenerateRhodesModelTask;
import rhogenwizard.sdk.task.GenerateRhodesSpecTask;

public class RhodesCreationTest extends TestCase
{
    private static final String workspaceFolder = new File(
            System.getProperty("java.io.tmpdir"), "junitworkfiles").getPath();

    private boolean checkCreateRhodesFile(String path)
    {
        String pathToBuildYml = path + File.separator + "build.yml";
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
    public void testCreateRhodesApp() throws Exception
    {
        String appName = "test001";

        Map<String, Object> params = new HashMap<String, Object>();

        params.put(GenerateRhodesAppTask.appName, appName);
        params.put(GenerateRhodesAppTask.workDir, workspaceFolder);

        Map<String, ?> results =
                RhoTaskHolder.getInstance().runTask(GenerateRhodesAppTask.class, params);

        assertEquals(TaskResultConverter.getResultIntCode(results), 0);

        assertEquals(checkCreateRhodesFile(workspaceFolder + File.separator + appName), true);
    }

    @Test
    public void testCreateRhodesModel() throws Exception
    {
        String appName = "test002";
        String modelName = "model002";
        String projectLoc = workspaceFolder + File.separator + appName;

        Map<String, Object> params = new HashMap<String, Object>();

        params.put(GenerateRhodesAppTask.appName, appName);
        params.put(GenerateRhodesAppTask.workDir, workspaceFolder);

        Map<String, ?> results =
                RhoTaskHolder.getInstance().runTask(GenerateRhodesAppTask.class, params);

        assertEquals(TaskResultConverter.getResultIntCode(results), 0);

        assertEquals(checkCreateRhodesFile(projectLoc), true);

        // create model
        params.clear();
        params = new HashMap<String, Object>();

        params.put(GenerateRhodesModelTask.modelName, modelName);
        params.put(GenerateRhodesModelTask.workDir, projectLoc);
        params.put(GenerateRhodesModelTask.modelFields, "a, b, c");

        Map<String, ?> modelResults =
                RhoTaskHolder.getInstance().runTask(GenerateRhodesModelTask.class, params);

        assertEquals(TaskResultConverter.getResultIntCode(modelResults), 0);
    }

    @Test
    public void testCreateRhodesExtension() throws Exception
    {
        String appName = "test005";
        String extensionName = "extension005";
        String projectLoc = workspaceFolder + File.separator + appName;

        // create application
        {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(GenerateRhodesAppTask.appName, appName);
            params.put(GenerateRhodesAppTask.workDir, workspaceFolder);

            Map<String, ?> results =
                    RhoTaskHolder.getInstance().runTask(GenerateRhodesAppTask.class, params);

            assertEquals(TaskResultConverter.getResultIntCode(results), 0);

            assertTrue(checkCreateRhodesFile(projectLoc));
        }

        // create extension
        {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(GenerateRhodesExtensionTask.extName, extensionName);
            params.put(GenerateRhodesExtensionTask.workDir, projectLoc);

            Map<String, ?> modelResults =
                    RhoTaskHolder.getInstance().runTask(GenerateRhodesExtensionTask.class,
                            params);

            assertEquals(TaskResultConverter.getResultIntCode(modelResults), 0);
        }
    }

    @Test
    public void testCreateRhodesSpec() throws Exception
    {
        String appName = "test006";
        String projectLoc = workspaceFolder + File.separator + appName;

        // create application
        {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(GenerateRhodesAppTask.appName, appName);
            params.put(GenerateRhodesAppTask.workDir, workspaceFolder);

            Map<String, ?> results =
                    RhoTaskHolder.getInstance().runTask(GenerateRhodesAppTask.class, params);

            assertEquals(TaskResultConverter.getResultIntCode(results), 0);

            assertTrue(checkCreateRhodesFile(projectLoc));
        }

        // create spec
        {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(GenerateRhodesSpecTask.workDir, projectLoc);

            Map<String, ?> specResults =
                    RhoTaskHolder.getInstance().runTask(GenerateRhodesSpecTask.class, params);

            assertEquals(TaskResultConverter.getResultIntCode(specResults), 0);
        }
    }
}
