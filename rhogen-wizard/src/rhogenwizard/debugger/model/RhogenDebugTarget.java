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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.ConsoleHandler;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IExpressionListener;
import org.eclipse.debug.core.IExpressionManager;
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
import org.eclipse.debug.core.model.IWatchExpressionResult;
import org.eclipse.debug.internal.core.WatchExpression;
import org.eclipse.dltk.internal.debug.core.model.NoWatchExpressionResult;
import org.eclipse.dltk.internal.debug.core.model.ScriptLineBreakpoint;
import org.eclipse.swt.graphics.Resource;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.constants.ConfigurationConstants;
import rhogenwizard.constants.DebugConstants;
import rhogenwizard.debugger.DebugServer;
import rhogenwizard.debugger.DebugState;
import rhogenwizard.debugger.DebugVariable;
import rhogenwizard.debugger.DebugVariableType;
import rhogenwizard.debugger.IDebugCallback;
import rhogenwizard.debugger.RhogenWatchExpressionResult;

/**
 * PDA Debug Target
 */
public class RhogenDebugTarget extends RhogenDebugElement implements IDebugTarget, IDebugCallback, IExpressionListener
{
	// associated system process (VM)
	private IProcess m_processHandle;
	
	// containing launch object
	private ILaunch m_launchHandle;
	
	// program name
	private String m_programName;
	
	// suspend state
	private boolean m_isSuspended = true;
	
	// terminated state
	private boolean m_isTerminated = false;
	
	// threads
	private RhogenThread m_threadHandle;
	private IThread[]    m_allThreads;
	
	private static DebugServer m_debugServer = null;

	public RhogenDebugTarget(ILaunch launch, IProcess process) throws CoreException 
	{
		super(null);
		
		m_launchHandle  = launch;
		fTarget         = this;
		m_processHandle = process;

		m_threadHandle = new RhogenThread(this);
		m_allThreads   = new IThread[] {m_threadHandle};
				
		DebugServer.setDebugOutputStream(System.out);
		
		if (m_debugServer != null)
		{
			m_debugServer.shutdown();
		}

		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
		DebugPlugin.getDefault().getExpressionManager().addExpressionListener(this);
		
		m_debugServer = new DebugServer(this);
		m_debugServer.start();
	}
	
	public void setProcess(IProcess p) 
	{
		m_processHandle = p;
	}
	
	public IProcess getProcess()
	{
		return m_processHandle;
	}

	public IThread[] getThreads() throws DebugException 
	{
		return m_allThreads;
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
		if (m_programName == null) 
		{
			try 
			{
				m_programName = getLaunch().getLaunchConfiguration().getAttribute(ConfigurationConstants.projectNameCfgAttribute, "");
			} 
			catch (CoreException e) 
			{
				m_programName = "";
			}
		}
		
		return m_programName;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#supportsBreakpoint(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public boolean supportsBreakpoint(IBreakpoint breakpoint) 
	{
		if (breakpoint.getModelIdentifier().equals(DebugConstants.debugModelId)) 
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
		return m_launchHandle;
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
		return m_isSuspended;
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
		m_isSuspended = false;
		m_threadHandle.fireResumeEvent(detail);
	}
	
	/**
	 * Notification the target has suspended for the given reason
	 * 
	 * @param detail reason for the suspend
	 */
	private void suspended(int detail) 
	{
		m_isSuspended = true;
		m_threadHandle.fireSuspendEvent(detail);
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
				ScriptLineBreakpoint lineBr = (ScriptLineBreakpoint) breakpoint;
				
				int    lineNum = lineBr.getLineNumber();
				String srcFile = prepareResNameForDebugger(lineBr.getResourcePath().toOSString());
				
				m_debugServer.debugRemoveBreakpoint(srcFile, lineNum);
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
		m_threadHandle.setStepping(true);
		resumed(DebugEvent.STEP_OVER);
		m_debugServer.debugStepOver();
	}
	
	public void stepInto()
	{
		m_threadHandle.setStepping(true);
		resumed(DebugEvent.STEP_INTO);
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
	
	static private String prepareResNameForDebugger(String resName)
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
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(DebugConstants.debugModelId);
		
		for (int i = 0; i < breakpoints.length; i++)
		{
			breakpointAdded(breakpoints[i]);
		}
	}
	
	private void installDeferredWatchs()
	{
		IExpression[] watchs = DebugPlugin.getDefault().getExpressionManager().getExpressions(DebugConstants.debugModelId);
		
		for (int i = 0; i < watchs.length; i++)
		{
			expressionAdded(watchs[i]);
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
		//ConsoleHelper.consoleAppPrint("file="+m_debugServer.debugGetFile()+" line="+m_debugServer.debugGetLine());
		
		stackData.m_currVariables = m_debugServer.debugWatchList();
		
		IStackFrame[] theFrames = new IStackFrame[1];
		theFrames[0] = new RhogenStackFrame(m_threadHandle, stackData, 0);
		
		return theFrames;
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
	public void stopped(DebugState state, String file, int line, String className, String method)
	{
		cleanState();
		
		installDeferredWatchs();
		
		if (state == DebugState.BREAKPOINT)
		{
			IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(DebugConstants.debugModelId);
		
			for (int i = 0; i < breakpoints.length; i++) 
			{
				IBreakpoint breakpoint = breakpoints[i];
	
				if (breakpoint instanceof ScriptLineBreakpoint)
				{
					ScriptLineBreakpoint lineBreakpoint = (ScriptLineBreakpoint) breakpoint;
					String resPath = prepareResNameForDebugger(lineBreakpoint.getResourcePath().toOSString());
					
					try 
					{
						if (lineBreakpoint.getLineNumber() == line && resPath.equals(file))
						{
							m_threadHandle.setBreakpoints(new IBreakpoint[]{breakpoint});
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
		else if (DebugState.paused(state))
		{
			m_threadHandle.setStepping(true);
			suspended(DebugEvent.STEP_END);
		}		
	}

	@Override
	public void unknown(String cmd) 
	{
	}

	@Override
	public void exited() 
	{
		m_isTerminated = true;
		m_isSuspended = false;
		
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
		DebugPlugin.getDefault().getExpressionManager().removeExpressionListener(this);
		
		fireTerminateEvent();
		m_debugServer.shutdown();
	}

	@Override
	public void resumed() 
	{
		cleanState();
		m_isSuspended = false;
		resumed(DebugEvent.CLIENT_REQUEST);
	}

	void cleanState()
	{
		m_threadHandle.setBreakpoints(null);
		m_threadHandle.setStepping(false);
	}
	
	@Override
	public void evaluation(boolean valid, String code, String value) 
	{
		IExpressionManager expManager = DebugPlugin.getDefault().getExpressionManager();
		
		IExpression[] modelExps = expManager.getExpressions(DebugConstants.debugModelId);
		
		for (IExpression currExp : modelExps)
		{
			String s = currExp.getExpressionText();
			
			if (currExp.getExpressionText().equals(code))
			{
				if (currExp instanceof WatchExpression)
				{
					try 
					{
						IValue watchVal = new RhogenValue(this, value);
						WatchExpression watchExp  = (WatchExpression)currExp;
						Thread.sleep(200); //HOTFIX 
						watchExp.setResult(new RhogenWatchExpressionResult(code, watchVal));
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
			}
		}	
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

	@Override
	public void expressionAdded(IExpression expression)
	{
		String expText = expression.getExpressionText();
		m_debugServer.debugEvaluate(expText);
	}

	@Override
	public void expressionRemoved(IExpression expression) 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void expressionChanged(IExpression expression) 
	{
		String expText = expression.getExpressionText();
		m_debugServer.debugEvaluate(expText);
	}

	@Override
	public void watch(DebugVariableType type, String variable, String value) 
	{
		// TODO Auto-generated method stub
	}
}
