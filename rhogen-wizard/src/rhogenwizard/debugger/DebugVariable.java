package rhogenwizard.debugger;

public class DebugVariable {
	private final DebugVariableType type;
	private final String variable;
	private final String value;
	
	public DebugVariable(DebugVariableType type, String variable, String value) {
		this.type = type;
		this.variable = variable;
		this.value = value;
	}

	public DebugVariableType type() {
		return this.type;
	}

	public String variable() {
		return this.variable;
	}
	
	public String value() {
		return this.value;
	}
}
