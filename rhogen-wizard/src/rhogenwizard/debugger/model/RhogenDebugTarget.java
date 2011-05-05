/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Bjorn Freeman-Benson - initial API and implementation
 *******************************************************************************/
package rhogenwizard.debugger.model;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IExpression;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.dltk.internal.debug.core.model.ScriptLineBreakpoint;
import org.eclipse.swt.graphics.Resource;

import rhogenwizard.debugger.DebugServer;
import rhogenwizard.debugger.DebugVariableType;
import rhogenwizard.debugger.IDebugCallback;
import rhogenwizard.debugger.RhogenConstants;
import rhogenwizard.launcher.RhogenLaunchDelegate;

/**
 * PDA Debug Target
 */
public class RhogenDebugTarget extends RhogenDebugElement implements IDebugTarget, IDebugCallback
{
	// associated system process (VM)
	private IProcess fProcess;
	
	// containing launch object
	private ILaunch fLaunch;
	
	// program name
	private String fName;
	
	// suspend state
	private boolean fSuspended = true;
	
	// terminated state
	private boolean fTerminated = false;
	
	// threads
	private RhogenThread fThread;
	private IThread[]    fThreads;
	
	private static DebugServer m_debugServer = null;

	public RhogenDebugTarget(ILaunch launch, IProcess process) throws CoreException 
	{
		super(null);
		
		fLaunch  = launch;
		fTarget  = this;
		fProcess = process;

		fThread  = new RhogenThread(this);
		fThreads = new IThread[] {fThread};
				
		DebugServer.setDebugOutputStream(System.out);
		
		if (m_debugServer != null)
		{
			m_debugServer.shutdown();
		}
		
		m_debugServer = new DebugServer(this);
		m_debugServer.start();

		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
	}
	
	public void setProcess(IProcess p) 
	{
		fProcess = p;
	}
	
	public IProcess getProcess()
	{
		return fProcess;
	}

	public IThread[] getThreads() throws DebugException 
	{
		return fThreads;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#hasThreads()
	 */
	public boolean hasThreads() throws DebugException 
	{
		return true; // WTB Changed per bug #138600
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#getName()
	 */
	public String getName() throws DebugException 
	{
		if (fName == null) 
		{
			try 
			{
				fName = getLaunch().getLaunchConfiguration().getAttribute(RhogenLaunchDelegate.projectNameCfgAttribute, "");
			} 
			catch (CoreException e) 
			{
				fName = "";
			}
		}
		
		return fName;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#supportsBreakpoint(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public boolean supportsBreakpoint(IBreakpoint breakpoint) 
	{
		if (breakpoint.getModelIdentifier().equals(RhogenConstants.debugModelId)) 
		{
			return true;
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	public IDebugTarget getDebugTarget() 
	{
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	public ILaunch getLaunch() 
	{
		return fLaunch;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() 
	{
		return getProcess().canTerminate();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() 
	{
		return getProcess().isTerminated();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException 
	{
		m_debugServer.debugTerminate();
		m_debugServer.shutdown();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume() 
	{
		return !isTerminated() && isSuspended();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend() 
	{
		return !isTerminated() && !isSuspended();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended()
	{
		return fSuspended;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException 
	{
		cleanState();
		resumed(DebugEvent.CLIENT_REQUEST);
		m_debugServer.debugResume();
	}
	
	/**
	 * Notification the target has resumed for the given reason
	 * 
	 * @param detail reason for the resume
	 */
	private void resumed(int detail) 
	{
		fSuspended = false;
		fThread.fireResumeEvent(detail);
	}
	
	/**
	 * Notification the target has suspended for the given reason
	 * 
	 * @param detail reason for the suspend
	 */
	private void suspended(int detail) 
	{
		fSuspended = true;
		fThread.fireSuspendEvent(detail);
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException 
	{
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointAdded(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public void breakpointAdded(IBreakpoint breakpoint) 
	{
		if (supportsBreakpoint(breakpoint)) 
		{
			try 
			{
				if (breakpoint.isEnabled()) 
				{
					ScriptLineBreakpoint lineBr = (ScriptLineBreakpoint) breakpoint;
					
					int    lineNum = lineBr.getLineNumber();
					String srcFile = prepareResNameForDebugger(lineBr.getResourcePath().toOSString());
					
					m_debugServer.debugBreakpoint(srcFile, lineNum);
				}
			} 
			catch (CoreException e) 
			{
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointRemoved(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) 
	{
		if (supportsBreakpoint(breakpoint)) 
		{
			try 
			{
				if (breakpoint.isEnabled()) 
				{
					ScriptLineBreakpoint lineBr = (ScriptLineBreakpoint) breakpoint;
					
					int    lineNum = lineBr.getLineNumber();
					String srcFile = prepareResNameForDebugger(lineBr.getResourcePath().toOSString());
					
					m_debugServer.debugRemoveBreakpoint(srcFile, lineNum);
				}
			} 
			catch (CoreException e) 
			{
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointChanged(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) 
	{
		if (supportsBreakpoint(breakpoint)) 
		{
			try 
			{
				if (breakpoint.isEnabled()) 
				{
					breakpointAdded(breakpoint);
				} 
				else 
				{
					breakpointRemoved(breakpoint, null);
				}
			} 
			catch (CoreException e) 
			{
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#canDisconnect()
	 */
	public boolean canDisconnect() 
	{
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#disconnect()
	 */
	public void disconnect() throws DebugException 
	{
	}
	
	public void stepOver()
	{
		cleanState();
		fThread.setStepping(true);
		m_debugServer.debugStepOver();
	}
	
	public void stepInto()
	{
		cleanState();
		fThread.setStepping(true);
		m_debugServer.debugStepInto();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#isDisconnected()
	 */
	public boolean isDisconnected() 
	{
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#supportsStorageRetrieval()
	 */
	public boolean supportsStorageRetrieval() 
	{
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#getMemoryBlock(long, long)
	 */
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException 
	{
		return null;
	}
	
	static String prepareResNameForDebugger(String resName)
	{
		resName = resName.replace('\\', '/');
		String[] segments = resName.split("app/");
		
		if (segments.length > 1)
			return segments[1];
		
		return segments[0];
	}
	
	/**
	 * Install breakpoints that are already registered with the breakpoint
	 * manager.
	 */
	private void installDeferredBreakpoints()
	{
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(RhogenConstants.debugModelId);
		
		for (int i = 0; i < breakpoints.length; i++)
		{
			breakpointAdded(breakpoints[i]);
		}
	}
	
	/**
	 * Returns the current stack frames in the target.
	 * 
	 * @return the current stack frames in the target
	 * @throws DebugException if unable to perform the request
	 */
	protected IStackFrame[] getStackFrames() throws DebugException 
	{
		StackData stackData = new StackData(m_debugServer.debugGetFile(), m_debugServer.debugGetLine());
		
		IStackFrame[] theFrames = new IStackFrame[1];
		theFrames[0] = new RhogenStackFrame(fThread, stackData, 0);

		m_debugServer.debugGetVariables();
		
		return theFrames;
	}
	
	/**
	 * Returns the current value of the given variable.
	 * 
	 * @param variable
	 * @return variable value
	 * @throws DebugException if the request fails
	 */
	protected IValue getVariableValue(RhogenVariable variable) throws DebugException 
	{
		m_debugServer.debugEvaluate(variable.getName());
		
		return new RhogenValue(this, "");
	}
	
	@Override
	public void connected() 
	{
		try
		{
			cleanState();
			fireCreationEvent();
			installDeferredBreakpoints();
			resume();
		}  	
		catch (DebugException e) 
		{
		}
	}

	@Override
	public void breakpoint(String file, int lineNumber, String className, String method)
	{
		cleanState();
		
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(RhogenConstants.debugModelId);
	
		for (int i = 0; i < breakpoints.length; i++) 
		{
			IBreakpoint breakpoint = breakpoints[i];

			if (breakpoint instanceof ScriptLineBreakpoint)
			{
				ScriptLineBreakpoint lineBreakpoint = (ScriptLineBreakpoint) breakpoint;
				String resPath = prepareResNameForDebugger(lineBreakpoint.getResourcePath().toOSString());
				
				try 
				{
					if (lineBreakpoint.getLineNumber() == lineNumber && resPath.equals(file))
					{
						fThread.setBreakpoints(new IBreakpoint[]{breakpoint});
						break;
					}
				}
				catch (CoreException e) 
				{
				}
			}
		}
		
		suspended(DebugEvent.BREAKPOINT);
	}

	@Override
	public void step(String file, int line, String className, String method)
	{
//		cleanState();
//        fThread.setStepping(true);
//        resumed(DebugEvent.STEP_OVER);
	}

	@Override
	public void unknown(String cmd) 
	{
	}

	@Override
	public void exited() 
	{
		fTerminated = true;
		fSuspended = false;
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
		fireTerminateEvent();
	}

	@Override
	public void resumed() 
	{
		cleanState();
		fSuspended = false;
		resumed(DebugEvent.CLIENT_REQUEST);
	}

	void cleanState()
	{
		fThread.setBreakpoints(null);
		fThread.setStepping(false);
	}
	
	@Override
	public void evaluation(boolean valid, String code, String value) 
	{
//		try 
//		{
//			IStackFrame[] frames = fThread.getStackFrames();
//			
//			for(int i=0; i<frames.length; ++i)
//			{
//				IStackFrame frame = frames[i];
//				
//				IVariable[] stackVars = frame.getVariables();
//				
//				for (int v=0; v<stackVars.length; ++v)
//				{
//					IVariable currVar = stackVars[v];
//					
//					if (currVar instanceof RhogenVariable)
//					{
//						if (currVar.getName().equals(code))
//						{
//							RhogenVariable rhoVar = (RhogenVariable) currVar;
//						
//							RhogenValue rhoValue = (RhogenValue) rhoVar.getValue();
//							rhoValue.setValue(value);
//						}
//					}
//				}
//			}
//		}
//		catch (DebugException e)
//		{
//			e.printStackTrace();
//		}
	}

	@Override
	public void watch(DebugVariableType type, String variable, String value) 
	{
		IValue val = new RhogenValue(this, value);
		DebugPlugin.getDefault().getExpressionManager().addExpression(new RhogenExpression(this, fLaunch, variable, val));
	}

	@Override
	public void watchBOL(DebugVariableType type) 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void watchEOL(DebugVariableType type) 
	{
		// TODO Auto-generated method stub
	}	
}
