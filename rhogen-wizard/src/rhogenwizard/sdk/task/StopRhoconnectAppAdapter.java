package rhogenwizard.sdk.task;

public class StopRhoconnectAppAdapter extends RunRhoconnectAppTask 
{
	private static final String taskTag = "stop-app-adapter";
	
	@Override
	public String getTag() { return taskTag; }

	@Override
	public void run() {}
	
	public static void stopRhoconnectApp() throws Exception
	{
		StopRhoconnectAppAdapter app = new StopRhoconnectAppAdapter();
		app.stopSyncApp();		
	}
}
