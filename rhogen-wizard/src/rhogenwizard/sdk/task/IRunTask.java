package rhogenwizard.sdk.task;

import java.util.Map;


public interface IRunTask extends Runnable 
{
	public static final String resTag = "result-code";
	public static final String workDir = "workdir";
	
	//
	void setData(Map<String, ?> data);
	//
	Map<String, ?> getResult();
}
