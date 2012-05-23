package rhogenwizard.debugger.model.selector;

import org.eclipse.dltk.internal.debug.core.model.ScriptLineBreakpoint;

public class RhodesResName implements IResName
{
	private ScriptLineBreakpoint m_bp;
	
	public RhodesResName(ScriptLineBreakpoint bp)
	{
		m_bp = bp;
	}
	
	@Override
	public String getResName() 
	{		
		return calcResName(m_bp.getResourcePath().toOSString(), "app/");
	}
	
	public String calcResName(String resName, String token) 
	{	
		resName = resName.replace('\\', '/');
		String[] segments = resName.split(token);
		
		if (segments.length > 1)
			return segments[1];
		
		return segments[0];
	}
	
	protected ScriptLineBreakpoint getBp()
	{
		return m_bp;
	}
}
