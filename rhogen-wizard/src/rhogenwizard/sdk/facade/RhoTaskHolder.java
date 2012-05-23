package rhogenwizard.sdk.facade;

import java.util.HashMap;
import java.util.Map;

import rhogenwizard.sdk.task.BuildPlatformTask;
import rhogenwizard.sdk.task.CleanAllPlatfromTask;
import rhogenwizard.sdk.task.CleanPlatformTask;
import rhogenwizard.sdk.task.CompileRubyPartTask;
import rhogenwizard.sdk.task.GenerateRhoconnectAdapterTask;
import rhogenwizard.sdk.task.GenerateRhoconnectAppTask;
import rhogenwizard.sdk.task.GenerateRhodesAppTask;
import rhogenwizard.sdk.task.GenerateRhodesExtensionTask;
import rhogenwizard.sdk.task.GenerateRhodesModelTask;
import rhogenwizard.sdk.task.GenerateRhodesSpecTask;
import rhogenwizard.sdk.task.GenerateRhoelementsAppTask;
import rhogenwizard.sdk.task.IRunTask;
import rhogenwizard.sdk.task.RunDebugRhoconnectAppTask;
import rhogenwizard.sdk.task.RunDebugRhodesAppTask;
import rhogenwizard.sdk.task.RunReleaseRhoconnectAppTask;
import rhogenwizard.sdk.task.RunReleaseRhodesAppTask;

public class RhoTaskHolder 
{
	private static RhoTaskHolder taskHolder = null;
	
	private Map<Class<? extends IRunTask>, IRunTask> m_holdTasks = null;
	
	public RhoTaskHolder()
	{
		m_holdTasks = new HashMap<Class<? extends IRunTask>, IRunTask>();
		
		// added tasks
		m_holdTasks.put(GenerateRhodesModelTask.class, new GenerateRhodesModelTask());
		m_holdTasks.put(RunReleaseRhodesAppTask.class, new RunReleaseRhodesAppTask());
		m_holdTasks.put(RunDebugRhodesAppTask.class, new RunDebugRhodesAppTask());
		m_holdTasks.put(GenerateRhoconnectAppTask.class, new GenerateRhoconnectAppTask());
		m_holdTasks.put(GenerateRhoconnectAdapterTask.class, new GenerateRhoconnectAdapterTask());
		m_holdTasks.put(RunDebugRhoconnectAppTask.class, new RunDebugRhoconnectAppTask());
		m_holdTasks.put(RunReleaseRhoconnectAppTask.class, new RunReleaseRhoconnectAppTask());
		m_holdTasks.put(CleanPlatformTask.class, new CleanPlatformTask());
		m_holdTasks.put(CleanAllPlatfromTask.class, new CleanAllPlatfromTask());	
		m_holdTasks.put(CompileRubyPartTask.class, new CompileRubyPartTask());
		m_holdTasks.put(BuildPlatformTask.class, new BuildPlatformTask());
		m_holdTasks.put(GenerateRhodesSpecTask.class, new GenerateRhodesSpecTask());
		m_holdTasks.put(GenerateRhodesExtensionTask.class, new GenerateRhodesExtensionTask());
	}
	
	public static RhoTaskHolder getInstance()
	{
		if (taskHolder == null)
		{
			taskHolder = new RhoTaskHolder();
		}
		
		return taskHolder;
	}
	
	public Map<String, ?> runTask(Class<? extends IRunTask> id, Map<String, Object> params)
	{
		IRunTask task = m_holdTasks.get(id);
		
		if (task != null)
		{
			task.setData(params);
			task.run();
			return task.getResult();
		}

		throw new IndexOutOfBoundsException();
	}
	
	public void stopTask(Class<? extends IRunTask> id)
	{
		IRunTask task = m_holdTasks.get(id);
		
		if (task != null)
		{
			task.stop();
			return;
		}

		throw new IndexOutOfBoundsException();
	}
}
