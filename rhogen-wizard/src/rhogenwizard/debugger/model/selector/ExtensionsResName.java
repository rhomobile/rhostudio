package rhogenwizard.debugger.model.selector;

import org.eclipse.dltk.internal.debug.core.model.ScriptLineBreakpoint;

public class ExtensionsResName implements IResName
{
	private static String extToken = "extensions/";
	
	private ScriptLineBreakpoint m_bp;
	
	public ExtensionsResName(ScriptLineBreakpoint bp)
	{
		m_bp = bp;
	}
	
	@Override
	public String getResName() 
	{		
		return calcResName(m_bp.getResourcePath().toOSString(), extToken);
	}
	
	public String calcResName(String resName, String token) 
	{	
		resName = resName.replace('\\', '/');
		String[] segments = resName.split(token);
		
		if (segments.length > 1)
			return extToken + segments[1];
		else
			return null;		
	}
	
	protected ScriptLineBreakpoint getBp()
	{
		return m_bp;
	}
}
