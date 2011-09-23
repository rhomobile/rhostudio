package rhogenwizard.sdk.task;

import java.util.Map;


public interface RunTask extends Runnable 
{
	public static final String resTag = "result-code";
	public static final String workDir = "workdir";
	
	//
	String getTag();
	//
	void setData(Map<String, String> data);
	//
	Map<String, String> getResult();
}
