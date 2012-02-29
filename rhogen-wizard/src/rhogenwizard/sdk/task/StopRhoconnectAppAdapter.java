package rhogenwizard.sdk.task;

public class StopRhoconnectAppAdapter extends RunRhoconnectAppTask 
{
	@Override
	public void run() {}
	
	public static void stopRhoconnectApp() throws Exception
	{
		StopRhoconnectAppAdapter app = new StopRhoconnectAppAdapter();
		app.stopSyncApp();		
	}
}
