package rhogenwizard.debugger;

/**
 * Ruby variable type:
 * {@link DebugVariableType#GLOBAL},
 * {@link DebugVariableType#LOCAL},
 * {@link DebugVariableType#CLASS},
 * {@link DebugVariableType#INSTANCE}.
 * @author Albert R. Timashev
 */
public enum DebugVariableType {
	/**
	 * Global variable (like: $global) 
	 */
	GLOBAL,
	/**
	 * Local variable (like: local)
	 */
	LOCAL,
	/**
	 * Class (static) variable (like: @@class_variable)
	 */
	CLASS,
	/**
	 * Instance variable (like: @object_variable)
	 */
	INSTANCE
}
