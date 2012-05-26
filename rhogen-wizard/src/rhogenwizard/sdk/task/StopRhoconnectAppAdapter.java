package rhogenwizard.sdk.task;

public class StopRhoconnectAppAdapter extends RunRhoconnectAppTask
{
    @Override
    protected void exec()
    {
    }

    public static void stopRhoconnectApp() throws Exception
    {
        StopRhoconnectAppAdapter app = new StopRhoconnectAppAdapter();
        app.stopSyncApp();
    }
}
