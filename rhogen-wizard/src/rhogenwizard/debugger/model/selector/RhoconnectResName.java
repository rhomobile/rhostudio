package rhogenwizard.debugger.model.selector;

import org.eclipse.dltk.internal.debug.core.model.ScriptLineBreakpoint;

public class RhoconnectResName implements IResName
{
	private ScriptLineBreakpoint m_bp;
	
	public RhoconnectResName(ScriptLineBreakpoint bp)
	{
		m_bp = bp;
	}
	
	@Override
	public String getResName() 
	{
		String[] srcName = m_bp.getResourcePath().segments();

		StringBuilder sb = new StringBuilder();
		sb.append('/');
		
		for (int i=1; i<srcName.length; i++)
		{
			sb.append(srcName[i]);
			sb.append('/');
		}
		
		sb.deleteCharAt(sb.length() - 1);
		
		return sb.toString();
	}
}
