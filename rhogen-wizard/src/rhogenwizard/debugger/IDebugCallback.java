package rhogenwizard.debugger;

/**
 * Interface of an object class to handle the events from the debugger extension
 * of Rhodes application.
 * @author Albert R. Timashev
 */
public interface IDebugCallback {
	/**
	 * Called when the Rhodes application connects to the debug server.
	 */
	public void connected();

	/**
	 * Called when the breakpoint is reached or after execution of next method
	 * of Ruby code after the {@link DebugServer#debugStepOver()},
	 * {@link DebugServer#debugStepInto()}, {@link DebugServer#debugStepReturn()},
	 * or {@link DebugServer#debugSuspend()} method call.
	 * @param state - indicates why the execution was stopped at this line.  
	 * @param file - file path within <code>app</code> folder of the Rhodes.
	 * application, e.g. <code>"application.rb"</code>.
	 * @param line - effective line number (starting with 1).
	 * @param className - class name of the current object ("" if none).
	 * @param method - name of the current method ("" if none).
	 */
	public void stopped(DebugState state, String file, int line, String className, String method);

	/**
	 * Called when Rhodes application is resumed after
	 * {@link DebugServer#debugResume()}, {@link DebugServer#debugStepInto()},
	 * {@link DebugServer#debugStepOver()} or {@link DebugServer#debugStepReturn()}
	 * method call.
	 */
	public void resumed();
	
	/**
	 * Called after the {@link DebugServer#debugEvaluate(String)} method call.
	 * @param valid - <code>true</code> if evaluated successfully,
	 * <code>false</code> otherwise. 
	 * @param code - original Ruby code.
	 * @param value - the resulted value of the evaluated/executed Ruby code
	 * or the error message if evaliation failed (when <code>valid</code>
	 * is <code>false</code>). 
	 */
	public void evaluation(boolean valid, String code, String value);
	
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
	 * {@link DebugServer#debugGetVariables()} or
	 * {@link DebugServer#debugGetVariables(DebugVariableType[])}. 
	 * @param type - Ruby variable type {@link DebugVariableType}.
	 * @param variable - name of the local variable.
	 * @param value - current value of the local variable.
	 */
	public void watch(DebugVariableType type, String variable, String value);

	/**
	 * Watch Begin-Of-List. Called before the first
	 * {@link #watch(DebugVariableType, String, String)}
	 * callback for the particular type of variables.  
	 * @param type - type of variables ({@link DebugVariableType}). 
	 */
	public void watchBOL(DebugVariableType type);

	/**
	 * Watch End-Of-List. Called after the last
	 * {@link #watch(DebugVariableType, String, String)}
	 * callback for the particular type of variables.  
	 * @param type - type of variables ({@link DebugVariableType}). 
	 */
	public void watchEOL(DebugVariableType type);
}
