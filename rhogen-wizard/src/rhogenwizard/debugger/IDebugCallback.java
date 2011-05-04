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
	 * @param className - class name of the current object ("" if none).
	 * @param method - name of the current method ("" if none).
	 */
	public void breakpoint(String file, int line, String className, String method);

	/**
	 * Called after execution of next method of Ruby code after the
	 * {@link DebugServer#debugStepOver()} or {@link DebugServer#debugStepInto()} method call.  
	 * @param file - file path within <code>app</code> folder of the Rhodes application,
	 * e.g. <code>"application.rb"</code>.
	 * @param line - effective line number (starting with 1).
	 * @param className - class name of the current object ("" if none).
	 * @param method - name of the current method ("" if none).
	 */
	public void step(String file, int line, String className, String method);

	/**
	 * Called when Rhodes application is resumed after
	 * {@link DebugServer#debugResume()} method call.
	 */
	public void resumed();
	
	/**
	 * Called after the {@link DebugServer#debugEvaluate(String)} or
	 * {@link DebugServer#debugEvaluate(String, boolean)} method call.
	 * @param value - the resulted value of the evaluated/executed Ruby code. 
	 */
	public void evaluation(String value);

	/**
	 * Called after the {@link DebugServer#debugEvaluate(String, boolean)} 
	 * method call when second parameter is <code>true</code>.
	 * @param code - original Ruby code. 
	 * @param value - the resulted value of the evaluated/executed Ruby code. 
	 */
	public void evaluation(String code, String value);
	
	/**
	 * Called upon receiving an unidentified response from the Rhodes application.
	 * @param cmd - unidentified line.
	 */
	public void unknown(String cmd);

	/**
	 * Called just before Rhodes application exit.
	 */
	public void exited();

	/**
	 * Variable watch. Called for each variable after the call of
	 * {@link DebugServer#debugGetVariables()} or {@link DebugServer#debugGetVariables(DebugVariableType[])}. 
	 * @param type - Ruby variable type {@link DebugVariableType}.
	 * @param variable - name of the local variable.
	 * @param value - current value of the local variable.
	 */
	public void watch(DebugVariableType type, String variable, String value);
}
