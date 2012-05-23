package rhogenwizard.sdk.task;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rhogenwizard.ConsoleHelper;
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
        return OSHelper.concat(path, "build.yml").isFile();
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
    public void testCreateRhodesApp() throws Exception
    {
        String appName = "test001";

        GenerateRhodesAppTask task = new GenerateRhodesAppTask(workspaceFolder, appName);
        task.run();

        assertEquals(0, TaskResultConverter.getResultIntCode(task.getResult()));

        assertTrue(checkCreateRhodesFile(workspaceFolder + File.separator + appName));
    }

    @Test
    public void testCreateRhodesModel() throws Exception
    {
        String appName = "test002";
        String modelName = "model002";
        String projectLoc = workspaceFolder + File.separator + appName;

        // create application
        {
            Map<String, Object> params = new HashMap<String, Object>();

            GenerateRhodesAppTask task = new GenerateRhodesAppTask(workspaceFolder, appName);
            task.run();

            assertEquals(0, TaskResultConverter.getResultIntCode(task.getResult()));

            assertTrue(checkCreateRhodesFile(projectLoc));
        }

        // create model
        {
            Map<String, Object> params = new HashMap<String, Object>();
            params = new HashMap<String, Object>();

            params.put(GenerateRhodesModelTask.modelName, modelName);
            params.put(GenerateRhodesModelTask.workDir, projectLoc);
            params.put(GenerateRhodesModelTask.modelFields, "a, b, c");

            Map<String, ?> modelResults =
                    RhoTaskHolder.getInstance().runTask(GenerateRhodesModelTask.class, params);

            assertEquals(TaskResultConverter.getResultIntCode(modelResults), 0);

            // TODO: why model directory is capitalized?
            assertTrue(OSHelper.concat(projectLoc, "app", "Model002").isDirectory());
            assertTrue(OSHelper.concat(projectLoc, "app", "test", "model002_spec.rb").isFile());
        }
    }

    @Test
    public void testCreateRhodesExtension() throws Exception
    {
        String appName = "test005";
        String extensionName = "extension005";
        String projectLoc = workspaceFolder + File.separator + appName;

        // create application
        {
            GenerateRhodesAppTask task = new GenerateRhodesAppTask(workspaceFolder, appName);
            task.run();

            assertEquals(0, TaskResultConverter.getResultIntCode(task.getResult()));

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

            // TODO: why extension directory is capitalized?
            assertTrue(OSHelper.concat(projectLoc, "app", "Extension005Test").isDirectory());
            assertTrue(OSHelper.concat(projectLoc, "extensions", extensionName).isDirectory());
        }
    }

    @Test
    public void testCreateRhodesSpec() throws Exception
    {
        String appName = "test006";
        String projectLoc = workspaceFolder + File.separator + appName;

        // create application
        {
            GenerateRhodesAppTask task = new GenerateRhodesAppTask(workspaceFolder, appName);
            task.run();

            assertEquals(0, TaskResultConverter.getResultIntCode(task.getResult()));

            assertTrue(checkCreateRhodesFile(projectLoc));
        }

        // create spec
        {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(GenerateRhodesSpecTask.workDir, projectLoc);

            Map<String, ?> specResults =
                    RhoTaskHolder.getInstance().runTask(GenerateRhodesSpecTask.class, params);

            assertEquals(TaskResultConverter.getResultIntCode(specResults), 0);

            // TODO: why extension directory is capitalized?
            assertTrue(OSHelper.concat(projectLoc, "app", "mspec.rb").isFile());
            assertTrue(OSHelper.concat(projectLoc, "app", "spec_runner.rb").isFile());
            assertTrue(OSHelper.concat(projectLoc, "app", "SpecRunner").isDirectory());
        }
    }
}
