package rhogenwizard.sdk.task;

public interface JobNotificationMonitor 
{
	void onJobStop();
	//
	void onJobStart();
	//
	void onJobFinished();
}
