package rhogenwizard.debugger.model.selector;

import org.eclipse.core.runtime.CoreException;
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
		String srcName = null;
		
		try 
		{
			srcName = m_bp.getResourceName();
		}
		catch (CoreException e)
		{
			srcName = new String();
		}
		
		srcName = srcName.replace('\\', '/');
		srcName = srcName.substring(1, srcName.length());
		String[] srcPath = srcName.split("/");
		
		if (srcPath.length < 1)
			return "";
		
		StringBuilder sb = new StringBuilder();
		sb.append("/");
		
		for (int i=1; i<srcPath.length; ++i)
		{
			sb.append(srcPath[i]);
		
			if (i+1 < srcPath.length)
				sb.append('/');
		}
		
		return sb.toString();
	}
}
