package rhogenwizard.debugger.backend;

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
	INSTANCE;
	
	/**
	 * Get Ruby variable type by a char id.
	 * @param id -
	 * <code>'G'</code> for {@link DebugVariableType#GLOBAL},
	 * <code>'C'</code> for {@link DebugVariableType#CLASS},
	 * <code>'I'</code> for {@link DebugVariableType#INSTANCE},
	 * <code>'L'</code> for {@link DebugVariableType#LOCAL}.
	 * @return Returns variable type ({@link DebugVariableType}).
	 */
	public static DebugVariableType variableTypeById(char id) {
		switch (id) {
		case 'G': return DebugVariableType.GLOBAL;
		case 'C': return DebugVariableType.CLASS;
		case 'I': return DebugVariableType.INSTANCE;
		default: return DebugVariableType.LOCAL;
		}
	}

	/**
	 * Get Ruby variable type name by ({@link DebugVariableType}).
	 * @param type - variable type ({@link DebugVariableType}).
	 * @return Returns a {@link String} representing a name of the Ruby variable type: 
	 * <code>"global"</code> for {@link DebugVariableType#GLOBAL},
	 * <code>"class"</code> for {@link DebugVariableType#CLASS},
	 * <code>"instance"</code> for {@link DebugVariableType#INSTANCE},
	 * <code>"local"</code> for {@link DebugVariableType#LOCAL}.
	 */
	public static String getName(DebugVariableType type) {
		switch (type) {
		case GLOBAL: return "global";
		case CLASS: return "class";
		case INSTANCE: return "instance";
		default: return "local";
		}
	}
}
