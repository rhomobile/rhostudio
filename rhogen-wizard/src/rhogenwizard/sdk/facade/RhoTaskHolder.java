package rhogenwizard.sdk.facade;

import java.util.Map;

import rhogenwizard.sdk.task.GenerateRhodesAppTask;
import rhogenwizard.sdk.task.RunTask;

public class RhoTaskHolder 
{
	private Map<String, RunTask> m_holdTasks = null;
	
	public RhoTaskHolder()
	{
		// added tasks
		m_holdTasks.put(GenerateRhodesAppTask.taskTag, new GenerateRhodesAppTask());
	}
	
	public Map<String, String> runTask(String id, Map<String, String> params)
	{
		RunTask task = m_holdTasks.get(id);
		
		if (task != null)
		{
			task.setData(params);
			task.run();
			return task.getResult();
		}
		
		return null;
	}
}
