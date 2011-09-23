package rhogenwizard.sdk.helper;

import java.util.Map;

import rhogenwizard.sdk.task.RunTask;

public class TaskResultConverter 
{
	public static int getResultIntCode(Map<String, String> results) throws Exception
	{
		String value = results.get(RunTask.resTag);
		
		if (value == null)
			throw new Exception();
		
		return Integer.parseInt(value);	
	}
}
