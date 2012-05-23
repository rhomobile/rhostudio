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
		return calcResName(getBp().getResourcePath().toOSString(), "framework/");		
	}
}
