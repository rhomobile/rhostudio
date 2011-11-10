package rhogenwizard.debugger.backend;

/**
 * The result of evaluation of Ruby expression or execution of Ruby code.  
 * @author Albert R. Timashev
 */
public class DebugEvaluation {
	private final boolean valid;
	private final String code;
	private final String value;
	
	public DebugEvaluation(boolean valid, String code, String value) {
		this.valid = valid;
		this.code = code;
		this.value = value;
	}

	/**
	 * The validity of evaluated expression/code.
	 * @return <code>true</code> if expression/code is valid, <code>false</code> otherwise.
	 */
	public boolean valid() {
		return this.valid;
	}

	/**
	 * The evaluated expression/code.
	 * @return Expression or code.
	 */
	public String code() {
		return this.code;
	}
	
	/**
	 * The resulting value.
	 * @return If <code>valid()</code> is <code>true</code>, then returns the resulting value. Otherwise returns the error message.
	 */
	public String value() {
		return this.value;
	}
}
