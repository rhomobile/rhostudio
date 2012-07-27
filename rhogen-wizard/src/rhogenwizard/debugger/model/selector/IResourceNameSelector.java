package rhogenwizard.debugger.model.selector;

import org.eclipse.dltk.internal.debug.core.model.ScriptLineBreakpoint;

public interface IResourceNameSelector 
{
	//
	String convertBpName(Class<?> projectTag, ScriptLineBreakpoint breakpoint);
}
