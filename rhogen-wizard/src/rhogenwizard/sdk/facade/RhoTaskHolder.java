package rhogenwizard.sdk.facade;

import java.util.HashMap;
import java.util.Map;

import rhogenwizard.sdk.task.RunTask;
import rhogenwizard.sdk.task.RunDebugRhoconnectAppTask;
import rhogenwizard.sdk.task.RunDebugRhodesAppTask;
import rhogenwizard.sdk.task.RunReleaseRhoconnectAppTask;
import rhogenwizard.sdk.task.RunReleaseRhodesAppTask;

public class RhoTaskHolder 
{
	private static RhoTaskHolder taskHolder = null;
	
	private Map<Class<? extends RunTask>, RunTask> m_holdTasks = null;
	
	public RhoTaskHolder()
	{
		m_holdTasks = new HashMap<Class<? extends RunTask>, RunTask>();
		
		// added tasks
		m_holdTasks.put(RunReleaseRhodesAppTask.class, new RunReleaseRhodesAppTask());
		m_holdTasks.put(RunDebugRhodesAppTask.class, new RunDebugRhodesAppTask());
		m_holdTasks.put(RunDebugRhoconnectAppTask.class, new RunDebugRhoconnectAppTask());
		m_holdTasks.put(RunReleaseRhoconnectAppTask.class, new RunReleaseRhoconnectAppTask());
	}
	
	public static RhoTaskHolder getInstance()
	{
		if (taskHolder == null)
		{
			taskHolder = new RhoTaskHolder();
		}
		
		return taskHolder;
	}
	
	public Map<String, ?> runTask(Class<? extends RunTask> id, Map<String, Object> params)
	{
		RunTask task = m_holdTasks.get(id);
		
		if (task != null)
		{
			task.setData(params);
			task.run();
			return task.getResult();
		}

		throw new IndexOutOfBoundsException();
	}
	
	public void stopTask(Class<? extends RunTask> id)
	{
		RunTask task = m_holdTasks.get(id);
		
		if (task != null)
		{
			task.stop();
			return;
		}

		throw new IndexOutOfBoundsException();
	}
}
