package rhogenwizard.debugger;

/**
 * Possible debug states of the Rhodes application:
 * {@link DebugState#NOTCONNECTED},
 * {@link DebugState#CONNECTED},
 * {@link DebugState#RUNNING},
 * {@link DebugState#BREAKPOINT},
 * {@link DebugState#STEP},
 * {@link DebugState#EXITED}. 
 * @author Albert R. Timashev
 */
public enum DebugState {
	/**
	 * Rhodes application is not connected yet.
	 */
	NOTCONNECTED,
	/**
	 * Rhodes application has been connected.
	 */
	CONNECTED,
	/**
	 * Rhodes application is currently running.
	 */
	RUNNING,
	/**
	 * Rhodes application is stopped at breakpoint.
	 */
	BREAKPOINT,
	/**
	 * Rhodes application is stopped after 'step' command.
	 */
	STEP,
	/**
	 * Rhodes application is exited (terminated).
	 */
	EXITED
}
