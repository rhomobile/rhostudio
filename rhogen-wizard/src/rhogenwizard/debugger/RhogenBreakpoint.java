package rhogenwizard.debugger;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;

public class RhogenBreakpoint implements IBreakpoint 
{
	private static final String breakMarkerId = "com.rhomobile.rhostudio.debugLineMarker";
	private static final String breakPointId = "com.rhomobile.rhostudio.debugBreakpoint";
	
	private IMarker m_marker = null;
	private boolean m_isEnabled = false;
	
    public RhogenBreakpoint(IResource resource, int lineNumber) throws CoreException 
    {
	     IMarker marker = resource.createMarker(breakMarkerId);
         setMarker(marker);
         setEnabled(true);
         getMarker().setAttribute(IMarker.LINE_NUMBER, lineNumber);
         getMarker().setAttribute(IBreakpoint.ID, breakPointId);
    }
	  
	@Override
	public Object getAdapter(Class adapter) 
	{
		return null;
	}

	@Override
	public void delete() throws CoreException {
		// TODO Auto-generated method stub
	}

	@Override
	public IMarker getMarker() 
	{
		return m_marker;
	}

	@Override
	public void setMarker(IMarker marker) throws CoreException
	{
		m_marker = marker;
	}

	@Override
	public String getModelIdentifier() 
	{
		return null;
	}

	@Override
	public boolean isEnabled() throws CoreException 
	{
		return m_isEnabled;
	}

	@Override
	public void setEnabled(boolean enabled) throws CoreException 
	{
		m_isEnabled = enabled;
	}

	@Override
	public boolean isRegistered() throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRegistered(boolean registered) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPersisted() throws CoreException 
	{
		return false;
	}

	@Override
	public void setPersisted(boolean registered) throws CoreException 
	{
	}
}
