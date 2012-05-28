package rhogenwizard.sdk.facade;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;

import rhogenwizard.sdk.task.RunTask;

public class RhoTaskHolder 
{
	private static RhoTaskHolder taskHolder = null;
	
	private Map<Class<? extends RunTask>, RunTask> m_holdTasks = null;
	
	public RhoTaskHolder()
	{
		m_holdTasks = new HashMap<Class<? extends RunTask>, RunTask>();
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
			task.run(new NullProgressMonitor());
			return task.getResult();
		}

		throw new IndexOutOfBoundsException();
	}
}
