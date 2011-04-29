package rhogenwizard.debugger;

public interface IDebugCallback {
	public void connected();
	public void breakpoint(String file, int line);
	public void evaluation(String value);
	public void unknown(String cmd);
	// public void exited(); - not yet implemented
}
