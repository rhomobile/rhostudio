package rhogenwizard.debugger.backend;

/**
 * Possible debug states of the Rhodes application:
 * {@link DebugState#NOTCONNECTED},
 * {@link DebugState#CONNECTED},
 * {@link DebugState#RUNNING},
 * {@link DebugState#BREAKPOINT},
 * {@link DebugState#STOPPED_INTO},
 * {@link DebugState#STOPPED_OVER},
 * {@link DebugState#STOPPED_RETURN},
 * {@link DebugState#SUSPENDED},
 * {@link DebugState#RESUMING},
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
	 * Rhodes application is stopped after 'step into' command.
	 */
	STOPPED_INTO,
	/**
	 * Rhodes application is stopped after 'step over' command.
	 */
	STOPPED_OVER,
	/**
	 * Rhodes application is stopped after 'step return' command.
	 */
	STOPPED_RETURN,
	/**
	 * Rhodes application is stopped after 'suspend' command.
	 */
	SUSPENDED,
	/**
	 * Rhodes application is resuming after stop (breakpoint,
	 * 'step' or 'suspend' command).
	 */
	RESUMING,
	/**
	 * Rhodes application is exited (terminated).
	 */
	EXITED;

	/**
	 * Check the Rhodes application debug status--is it paused or not.
	 * @param state - specified debug state.
	 * @return Returns <code>true</code> if execution of the application is suspended
	 * by stop at breakpoint, 'step' command or 'suspend' command.
	 * Returns <code>false</code> otherwise.  
	 */
	public static boolean paused(DebugState state) {
		return (state==DebugState.BREAKPOINT) || (state==DebugState.STOPPED_INTO) || (state==DebugState.STOPPED_OVER) || (state==DebugState.STOPPED_RETURN) || (state==DebugState.SUSPENDED);
	}

	/**
	 * Get debug state name by ({@link DebugState}).
	 * @param state - debug state ({@link DebugState}).
	 * @return Returns a {@link String} representing a name of the debug state: 
	 * <code>"disconnected"</code> for {@link DebugState#NOTCONNECTED},
	 * <code>"connected"</code> for {@link DebugState#CONNECTED},
	 * <code>"runnung"</code> for {@link DebugState#RUNNING},
	 * <code>"breakpoint"</code> for {@link DebugState#BREAKPOINT},
	 * <code>"stopped (into)"</code> for {@link DebugState#STOPPED_INTO},
	 * <code>"stopped (over)"</code> for {@link DebugState#STOPPED_OVER},
	 * <code>"stopped (return)"</code> for {@link DebugState#STOPPED_RETURN},
	 * <code>"suspended"</code> for {@link DebugState#SUSPENDED},
	 * <code>"resuming"</code> for {@link DebugState#RESUMING},
	 * <code>"terminated"</code> for {@link DebugState#EXITED}.
	 */
	public static String getName(DebugState state) {
		switch (state) {
		case NOTCONNECTED: return "disconnected";
		case CONNECTED: return "connected";
		case RUNNING: return "runnung";
		case BREAKPOINT: return "breakpoint";
		case STOPPED_INTO: return "stopped (into)";
		case STOPPED_OVER: return "stopped (over)";
		case STOPPED_RETURN: return "stopped (return)";
		case SUSPENDED: return "suspended";
		case RESUMING: return "resuming";
		default: return "terminated";
		}
	}
}
