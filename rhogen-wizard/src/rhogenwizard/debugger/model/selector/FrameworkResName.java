package rhogenwizard.debugger.model.selector;

import org.eclipse.dltk.internal.debug.core.model.ScriptLineBreakpoint;

public class FrameworkResName extends RhodesResName
{
	public FrameworkResName(ScriptLineBreakpoint bp)
	{
		super(bp);
	}
	
	@Override
	public String getResName() 
	{
		final String token = "framework/";
		
		return token + calcResName(getBp().getResourcePath().toOSString(), token);		
	}
}
