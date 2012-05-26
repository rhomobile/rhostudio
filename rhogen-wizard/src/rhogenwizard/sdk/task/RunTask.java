package rhogenwizard.sdk.task;

import java.util.Map;


public abstract class RunTask implements Runnable 
{
	public static final String resTag = "result-code";
	public static final String workDir = "workdir";
	
	 //
	public abstract void setData(Map<String, ?> data);
	//
	public abstract void stop();
	//
	public abstract Map<String, ?> getResult();	
}
