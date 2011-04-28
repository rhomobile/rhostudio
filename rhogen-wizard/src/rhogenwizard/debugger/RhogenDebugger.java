package rhogenwizard.debugger;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.w3c.dom.views.AbstractView;

import rhogenwizard.OSHelper;

public class RhogenDebugger implements IDebugTarget 
{
	private ILaunch  m_parentLaunch = null;
	private IProcess m_runProcess = null;
	private IThread  m_mainThread = null;
	
	public RhogenDebugger(ILaunch launch, IProcess runProcess) 
	{
		m_parentLaunch = launch;
		m_runProcess   = runProcess;
		m_mainThread   = new RhogenThread();
		
		initializeDebugger();
		
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
	}
	
	private void initializeDebugger()
	{
	}
	
	@Override
	public String getModelIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDebugTarget getDebugTarget() 
	{
		return this;
	}

	@Override
	public ILaunch getLaunch() 
	{
		return m_parentLaunch;
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canTerminate() 
	{
		return m_runProcess.canTerminate();
	}

	@Override
	public boolean isTerminated() 
	{
		return false; 
	}

	@Override
	public void terminate() throws DebugException 
	{
		try 
		{
			OSHelper.killProcess("rhodes");
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean canResume() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canSuspend() 
	{
		return false;
	}

	@Override
	public boolean isSuspended() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resume() throws DebugException {
		// TODO Auto-generated method stub
	}

	@Override
	public void suspend() throws DebugException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canDisconnect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void disconnect() throws DebugException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isDisconnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportsStorageRetrieval() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException 
	{
		return null;
	}

	@Override
	public IProcess getProcess() 
	{
		return m_runProcess;
	}

	@Override
	public IThread[] getThreads() throws DebugException 
	{
		IThread[] threads = {m_mainThread};
		return threads;
	}

	@Override
	public boolean hasThreads() throws DebugException 
	{
		return false;
	}

	@Override
	public String getName() throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void breakpointAdded(IBreakpoint breakpoint) 
	{
	    if (supportsBreakpoint(breakpoint)) 
	    {	
	        try 
	        {
	        	if (breakpoint.isEnabled()) 
	        	{
	        		/*
	        		synchronized (fRequestSocket) 
	        		{
	        			try 
	        			{
	        				sendRequest("set " + (((ILineBreakpoint)breakpoint).getLineNumber() - 1));
	        			} 
	        			catch (CoreException e) 
	        			{
	        			}
	        		}
	        		*/
	        	}
	        } 
	        catch (CoreException e) 
	        {
	        }
	    }
	}

	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) 
	{
		IMarkerDelta delta1 = delta;
		delta1.getId();
		// TODO Auto-generated method stub
	}

	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) 
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean supportsBreakpoint(IBreakpoint breakpoint) 
	{
		/*
        if (breakpoint.getModelIdentifier().equals(IPDAConstants.ID_PDA_DEBUG_MODEL)) 
        {
            try 
            {
                String program = getLaunch().getLaunchConfiguration()
                     .getAttribute(IPDAConstants.ATTR_PDA_PROGRAM, (String)null);
            
                if (program != null) 
                {
                    IMarker marker = breakpoint.getMarker();
               
                    if (marker != null) 
                    {
                        IPath p = new Path(program);
                        return marker.getResource().getFullPath().equals(p);
                    }
                }
            } 
            catch (CoreException e) 
            {
            } 
        }
        
        return false;
        */
		
		return true;
	}	
}
