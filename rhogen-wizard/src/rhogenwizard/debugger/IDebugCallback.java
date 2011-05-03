package rhogenwizard.debugger;

/**
 * Interface of an object class to handle the events from the Rhodes application.
 * @author Albert R. Timashev
 */
public interface IDebugCallback {
	/**
	 * Called when the Rhodes application connects to the debug server.
	 */
	public void connected();

	/**
	 * Called when the breakpoint is reached.
	 * @param file - file path within <code>app</code> folder of the Rhodes application,
	 * e.g. <code>"application.rb"</code>.
	 * @param line - effective line number (starting with 1).
	 */
	public void breakpoint(String file, int line);

	/**
	 * Called after execution of the one line of Ruby code after the {@link DebugServer#debugStep()} method call.  
	 * @param file - file path within <code>app</code> folder of the Rhodes application,
	 * e.g. <code>"application.rb"</code>.
	 * @param line - effective line number (starting with 1).
	 */
	public void step(String file, int line);

	/**
	 * Called after the {@link DebugServer#debugEvaluate(String)} method call.
	 * @param value - the resulted value of the evaluated/executed Ruby code. 
	 */
	public void evaluation(String value);

	/**
	 * Called upon receiving an unidentified response from the Rhodes application.
	 * @param cmd - unidentified line.
	 */
	public void unknown(String cmd);

	/**
	 * Called just before Rhodes application exit.
	 */
	public void exited();
}
