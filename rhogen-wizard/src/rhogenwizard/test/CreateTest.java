package rhogenwizard.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rhogenwizard.OSHelper;
import rhogenwizard.constants.MsgConstants;
import rhogenwizard.sdk.facade.RhoTaskHolder;
import rhogenwizard.sdk.helper.TaskResultConverter;
import rhogenwizard.sdk.task.GenerateRhoconnectAdapterTask;
import rhogenwizard.sdk.task.GenerateRhoconnectAppTask;
import rhogenwizard.sdk.task.GenerateRhodesAppTask;
import rhogenwizard.sdk.task.GenerateRhodesModelTask;

public class CreateTest extends TestCase
{
	private static final String worspaceFolder = "c:\\android\\junitworkfiles";

	boolean checkCreateRhodesFile(String path)
	{
		String pathToBuildYml = path + File.separator + "build.yml";
		File f = new File(pathToBuildYml);
		
		return f.isFile();
	}
	
	boolean checkCreateRhoconnectFile(String path)
	{
		String pathToBuildYml = path + File.separator + "config.ru";
		File f = new File(pathToBuildYml);
		
		return f.isFile();		
	}
	
	@Before
	public void setUp() throws Exception
	{
		OSHelper.deleteFolder(worspaceFolder);
		
		File newWsFodler = new File(worspaceFolder);
		newWsFodler.mkdir();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
    public void test1CreateRhodesApp()
    {
		String appName =  "test001";
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put(GenerateRhodesAppTask.appName, appName);
		params.put(GenerateRhodesAppTask.workDir, worspaceFolder);
		
		Map results = RhoTaskHolder.getInstance().runTask(GenerateRhodesAppTask.taskTag, params);
		
		try {
			assertEquals(TaskResultConverter.getResultIntCode(results), 0);
		} catch (Exception e) {
			fail("fail on check result [test1]");
		}
		
		assertEquals(checkCreateRhodesFile(worspaceFolder + File.separator + appName), true);		
    }

	@Test
    public void test2CreateRhodesModel()
    {
		String appName   =  "test002";
		String modelName =  "model002";
		String projectLoc = worspaceFolder + File.separator + appName;
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put(GenerateRhodesAppTask.appName, appName);
		params.put(GenerateRhodesAppTask.workDir, worspaceFolder);
		
		Map results = RhoTaskHolder.getInstance().runTask(GenerateRhodesAppTask.taskTag, params);
		
		try {
			assertEquals(TaskResultConverter.getResultIntCode(results), 0);
		} catch (Exception e) {
			fail("fail on check create app result [test2]");
		}
		
		assertEquals(checkCreateRhodesFile(projectLoc), true);
		
		// create model
		params.clear();
		params = new HashMap<String, Object>();
		
		params.put(GenerateRhodesModelTask.modelName, modelName);
		params.put(GenerateRhodesModelTask.workDir, projectLoc);
		params.put(GenerateRhodesModelTask.modelFields, "a, b, c");
		
		Map modelResults = RhoTaskHolder.getInstance().runTask(GenerateRhodesModelTask.taskTag, params);
		
		try {
			assertEquals(TaskResultConverter.getResultIntCode(modelResults), 0);
		} catch (Exception e) {
			fail("fail on check create model result [test2]");
		}	
    }

	@Test
    public void test3CreateRhoconnectApp()
    {
		String appName =  "test003";
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put(GenerateRhoconnectAppTask.appName, appName);
		params.put(GenerateRhoconnectAppTask.workDir, worspaceFolder);
		
		Map results = RhoTaskHolder.getInstance().runTask(GenerateRhoconnectAppTask.taskTag, params);
		
		try {
			assertEquals(TaskResultConverter.getResultIntCode(results), 0);
		} catch (Exception e) {
			fail("fail on check result [test3]");
		}
		
		assertEquals(checkCreateRhoconnectFile(worspaceFolder + File.separator + appName), true);		
    }
	
	@Test
    public void test4CreateRhoconnectSrcAdapter()
    {
		String appName =  "test004";
		String adapterName = "adapter001";
		String projectLocation = worspaceFolder + File.separator + appName;
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put(GenerateRhoconnectAppTask.appName, appName);
		params.put(GenerateRhoconnectAppTask.workDir, worspaceFolder);
		
		Map results = RhoTaskHolder.getInstance().runTask(GenerateRhoconnectAppTask.taskTag, params);
		
		try {
			assertEquals(TaskResultConverter.getResultIntCode(results), 0);
		} catch (Exception e) {
			fail("fail on check result [test4]");
		}
		
		assertEquals(checkCreateRhoconnectFile(projectLocation), true);		
				
		params.clear();		
		params.put(GenerateRhoconnectAdapterTask.sourceName, adapterName);
		params.put(GenerateRhoconnectAdapterTask.workDir, projectLocation);
		
		results = RhoTaskHolder.getInstance().runTask(GenerateRhoconnectAdapterTask.taskTag, params);

		try {
			assertEquals(TaskResultConverter.getResultIntCode(results), 0);
		} catch (Exception e) {
			fail("fail on check result [test4]");
		}
    }
}
