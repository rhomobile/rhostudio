package rhogenwizard.sdk.helper;

import java.util.Map;

import org.eclipse.debug.core.model.IProcess;

import rhogenwizard.sdk.task.IRunTask;
import rhogenwizard.sdk.task.RunDebugRhodesAppTask;

public class TaskResultConverter 
{
	public static int getResultIntCode(Map<String, ?> results) throws Exception
	{
		if (results == null)
			throw new Exception("result container is null [TaskResultConverter]");
		
		Object value = results.get(IRunTask.resTag);

		if (value == null)
			throw new Exception("return patameter is null [TaskResultConverter]");

		if (value instanceof Integer)
		{			
			Integer retCode = (Integer)value;
			
			return retCode.intValue();				
		}
		
		throw new Exception("invalid return parameter [TaskResultConverter]");
	}
	
	public static IProcess getResultLaunchObj(Map<String, ?> results) throws Exception
	{
		if (results == null)
			throw new Exception("result container is null [TaskResultConverter]");
		
		Object value = results.get(RunDebugRhodesAppTask.resProcess);

		if (value == null)
			throw new Exception("return patameter is null [TaskResultConverter]");

		if (value instanceof IProcess)
		{			
			return (IProcess)value;				
		}
		
		throw new Exception("invalid return parameter [TaskResultConverter]");
	}
}
