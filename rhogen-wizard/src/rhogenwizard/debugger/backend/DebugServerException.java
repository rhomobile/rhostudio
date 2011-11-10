package rhogenwizard.debugger.backend;

/**
 * This type of ecxeption is thrown when one of application execution control
 * methods of {@link DebugServer} is called while it is processing a
 * synchronous command (like {@link DebugServer#debugWatchList()}).
 * @author Albert R. Timashev
 */
public class DebugServerException extends RuntimeException {
	private static final long serialVersionUID = 2155265526637789313L;

	public DebugServerException(String message) {
		super(message);
	};
}
