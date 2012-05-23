package rhogenwizard.debugger.model.selector;

import org.eclipse.dltk.internal.debug.core.model.ScriptLineBreakpoint;

import rhogenwizard.project.RhoconnectProject;
import rhogenwizard.project.RhodesProject;

public class ResourceNameSelector implements IResourceNameSelector
{	
	static IResourceNameSelector m_instance = null;
	
	public static IResourceNameSelector getInstance()
	{
		if (m_instance == null)
		{
			m_instance = new ResourceNameSelector();
		}
		
		return m_instance;
	}
	
	@Override
	public String convertBpName(Class projectTag, ScriptLineBreakpoint breakpoint) 
	{	
		if (projectTag.equals(RhodesProject.class))
		{
			IResName rhodesName     = new RhodesResName(breakpoint); 
			IResName fwName         = new FrameworkResName(breakpoint);

			if(rhodesName.getResName() != null)
			{
				return rhodesName.getResName();
			}
			else if (fwName.getResName() != null)
			{
				return fwName.getResName();
			}
		}
		else if (projectTag.equals(RhoconnectProject.class))
		{
			IResName rhoconnectName = new RhoconnectResName(breakpoint);
			return rhoconnectName.getResName();
		}
		
		return null;
	}
}
