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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.dltk.internal.debug.core.model.ScriptLineBreakpoint;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.ShowOnlyHidePerspectiveJob;
import rhogenwizard.constants.ConfigurationConstants;
import rhogenwizard.constants.DebugConstants;
import rhogenwizard.debugger.RhogenWatchExpression;
import rhogenwizard.debugger.RhogenWatchExpressionResult;
import rhogenwizard.debugger.backend.DebugServer;
import rhogenwizard.debugger.backend.DebugServerException;
import rhogenwizard.debugger.backend.DebugState;
import rhogenwizard.debugger.backend.DebugVariableType;
import rhogenwizard.debugger.backend.IDebugCallback;
import rhogenwizard.debugger.model.selector.ResourceNameSelector;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhoconnectProject;
import rhogenwizard.project.extension.BadProjectTagException;
import rhogenwizard.sdk.task.StopSyncAppTask;

/**
 * PDA Debug Target
 */
public class DebugTarget extends DebugElement implements IDebugTarget, IDebugCallback, IExpressionListener
{
	private static String fwTag = "framework";
	
    private IProject           m_debugProject  = null;

    // associated system process (VM)
    private IProcess           m_processHandle = null;

    // containing launch object
    private ILaunch            m_launchHandle  = null;

    // program name
    private String             m_programName   = null;

    // suspend state
    private boolean            m_isSuspended   = true;

    // threads
    private DebugThread        m_threadHandle  = null;
    private IThread[]          m_allThreads    = null;

    private static DebugServer m_debugServer   = null;

    public DebugTarget(ILaunch launch, IProcess process, IProject debugProject)
    {
        super(null);

        m_debugProject = debugProject;
        m_launchHandle = launch;
        m_debugTarget = this;
        m_processHandle = process;

        m_threadHandle = new DebugThread(this);
        m_allThreads   = new IThread[] { m_threadHandle };

        DebugServer.setDebugOutputStream(System.out);

        if (m_debugServer != null)
        {
            m_debugServer.shutdown();
        }

        DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
        DebugPlugin.getDefault().getExpressionManager().addExpressionListener(this);

        m_debugServer = new DebugServer(this);
        m_debugServer.start();
        m_debugServer.debugSkipBreakpoints(false);
    }

    @Override
    protected void finalize() throws Throwable
    {
        exited();
        super.finalize();
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

    public boolean hasThreads() throws DebugException
    {
        return true;
    }

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

    public boolean supportsBreakpoint(IBreakpoint breakpoint)
    {
        if (breakpoint.getModelIdentifier().equals(DebugConstants.debugModelId))
        {
            return true;
        }

        return false;
    }

    public IDebugTarget getDebugTarget()
    {
        return this;
    }

    public ILaunch getLaunch()
    {
        return m_launchHandle;
    }

    public boolean canTerminate()
    {
        if (m_processHandle == null)
            return true;

        return m_processHandle.canTerminate();
    }

    public boolean isTerminated()
    {
        if (m_processHandle == null)
            return true;

        return m_processHandle.isTerminated();
    }

    public void terminate() throws DebugException
    {
        try
        {
            m_debugServer.debugTerminate();

            if (ProjectFactory.getInstance().typeFromProject(m_debugProject).equals(RhoconnectProject.class))
            {
                new StopSyncAppTask().run();
            }

            if (m_processHandle != null)
            {
                m_processHandle.terminate();
                m_processHandle = null;
            }
        }
        catch (DebugServerException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean canResume()
    {
        return !isTerminated() && isSuspended();
    }

    public boolean canSuspend()
    {
        return !isTerminated() && !isSuspended();
    }

    private void waitDebugProcessing()
    {
        while (m_debugServer.debugIsProcessing())
        {
        }
        
        try {
			Thread.currentThread().sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public boolean isSuspended()
    {
        return m_isSuspended;
    }

    public void resume() throws DebugException
    {
        waitDebugProcessing();
        cleanState();
        waitDebugProcessing();
        resumed(DebugEvent.CLIENT_REQUEST);
        waitDebugProcessing();
        m_debugServer.debugResume();
    }

    /**
     * Notification the target has resumed for the given reason
     * 
     * @param detail
     *            reason for the resume
     */
    private void resumed(int detail)
    {
        waitDebugProcessing();
        m_isSuspended = false;
        m_threadHandle.fireResumeEvent(detail);
    }

    /**
     * Notification the target has suspended for the given reason
     * 
     * @param detail
     *            reason for the suspend
     */
    private void suspended(int detail)
    {
        waitDebugProcessing();
        m_isSuspended = true;
        m_threadHandle.fireSuspendEvent(detail);
    }

    public void suspend() throws DebugException
    {
    }
    
    //TODO - hot fix 
    private boolean isFunctionDefinition(ScriptLineBreakpoint lineBp)
    {
        IFile file = (IFile) lineBp.getResource();
        
		try 
		{
	    	BufferedReader contentBuffer = new BufferedReader(new InputStreamReader(file.getContents()));
	    	
	    	String buf = "";
    	
    		for (int i=0; i < lineBp.getLineNumber(); i++)
    		{
    			buf = contentBuffer.readLine();
    		}
    		
    		return buf.matches("^\\s*def\\s.*$");
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
    	catch (IOException e) 
    	{
			e.printStackTrace();
		}	
		
		return false;
    }

    public void breakpointAdded(IBreakpoint breakpoint)
    {
        boolean globalBpEnable = DebugPlugin.getDefault().getBreakpointManager().isEnabled();
        
        if (supportsBreakpoint(breakpoint))
        {
            try
            {
                if (breakpoint.isEnabled() && globalBpEnable)
                {
                    ScriptLineBreakpoint lineBr = (ScriptLineBreakpoint) breakpoint;

                    if (!isFunctionDefinition(lineBr))
                    {
	                    int    lineNum = lineBr.getLineNumber();
	                    String srcFile = ResourceNameSelector.getInstance().convertBpName(ProjectFactory.getInstance().typeFromProject(m_debugProject), lineBr);
	
	                    m_debugServer.debugBreakpoint(srcFile, lineNum);
                    }
                    else
                    {
                    	breakpoint.setEnabled(false);
                    }
                }
            }
            catch (CoreException e)
            {
            }
            catch (BadProjectTagException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta)
    {
        if (supportsBreakpoint(breakpoint))
        {
            try
            {
                ScriptLineBreakpoint lineBr = (ScriptLineBreakpoint) breakpoint;

                int    lineNum = lineBr.getLineNumber();
                String srcFile = ResourceNameSelector.getInstance().convertBpName(ProjectFactory.getInstance().typeFromProject(m_debugProject), lineBr);

                m_debugServer.debugRemoveBreakpoint(srcFile, lineNum);
            }
            catch (CoreException e)
            {
            }
            catch (BadProjectTagException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta)
    {
        boolean globalBpEnable = DebugPlugin.getDefault().getBreakpointManager().isEnabled();
        
        if (supportsBreakpoint(breakpoint))
        {
            if (!globalBpEnable)
            {
                breakpointRemoved(breakpoint, null);
                return;
            }
            
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

    public boolean canDisconnect()
    {
        return false;
    }

    public void disconnect() throws DebugException
    {
    }

    public void stepOver()
    {
        waitDebugProcessing();
        m_threadHandle.setStepping(true);
        waitDebugProcessing();
        resumed(DebugEvent.STEP_OVER);
        waitDebugProcessing();
        m_debugServer.debugStepOver();
    }

    public void stepReturn()
    {
        waitDebugProcessing();
        m_threadHandle.setStepping(true);
        waitDebugProcessing();
        resumed(DebugEvent.STEP_RETURN);
        waitDebugProcessing();
        m_debugServer.debugStepReturn();
    }

    public void stepInto()
    {
        waitDebugProcessing();
        m_threadHandle.setStepping(true);
        waitDebugProcessing();
        resumed(DebugEvent.STEP_INTO);
        waitDebugProcessing();
        m_debugServer.debugStepInto();
    }

    public boolean isDisconnected()
    {
        return false;
    }

    public boolean supportsStorageRetrieval()
    {
        return false;
    }

    public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException
    {
        return null;
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
        IExpression[] watchs = DebugPlugin.getDefault().getExpressionManager().getExpressions();

        for (IExpression exp : watchs)
        {
            waitDebugProcessing();
            m_debugServer.debugEvaluate(exp.getExpressionText());
        }
    }

    /**
     * Returns the current stack frames in the target.
     * 
     * @return the current stack frames in the target
     * @throws DebugException
     *             if unable to perform the request
     */
    protected IStackFrame[] getStackFrames() throws DebugException
    {
        waitDebugProcessing();

        StackData stackData = new StackData(m_debugServer.debugGetFile(), m_debugServer.debugGetLine());

        IStackFrame[] theFrames = new IStackFrame[1];

        for (int i = 0; i < 3; ++i)
        {
            try
            {
                stackData.m_currVariables = new ArrayList<rhogenwizard.debugger.backend.DebugVariable>(); //m_debugServer.debugWatchList();
                theFrames[0] = new DebugStackFrame(m_threadHandle, stackData, 0);
                break;
            }
            catch (DebugServerException e)
            {
                try
                {
                    Thread.sleep(200);
                }
                catch (InterruptedException e1)
                {
                }
            }
        }

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

    boolean isFoundFramework()
    {
    	IFolder fwFodler = m_debugProject.getFolder(fwTag);
    	
		try
		{
			IResource[] childRes = fwFodler.members();
			
			return fwFodler.exists() && childRes.length != 0;
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
    	
		return false;
    }
    
    private void showDebugPerspective()
    {
    	ShowOnlyHidePerspectiveJob job = new ShowOnlyHidePerspectiveJob("Show debug perspective", DebugConstants.debugPerspectiveId);
    	job.schedule();
    }

    @Override
    public void stopped(DebugState state, String file, int line, String className, String method)
    {
        waitDebugProcessing();
        showDebugPerspective();
        
    	if (file.contains(fwTag))
    	{
    		if (!isFoundFramework())
    		{
    			try 
    			{
					resume();
				}
    			catch (DebugException e) 
    			{
					e.printStackTrace();
				}
    			
    			return;
    		}
    	}
    	
        cleanState();

        installDeferredWatchs();

        waitDebugProcessing();

        if (state == DebugState.BREAKPOINT)
        {
            IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(DebugConstants.debugModelId);

            for (int i = 0; i < breakpoints.length; i++)
            {
                waitDebugProcessing();

                IBreakpoint breakpoint = breakpoints[i];

                if (breakpoint instanceof ScriptLineBreakpoint)
                {
                    try
                    {
                        ScriptLineBreakpoint lineBreakpoint = (ScriptLineBreakpoint) breakpoint;
                        String resPath = ResourceNameSelector.getInstance().convertBpName(ProjectFactory.getInstance().typeFromProject(m_debugProject), lineBreakpoint);

                        if (lineBreakpoint.getLineNumber() == line && resPath.equals(file))
                        {
                            m_threadHandle.setBreakpoints(new IBreakpoint[] { breakpoint });
                            break;
                        }
                    }
                    catch (CoreException e)
                    {
                    }
                    catch (BadProjectTagException e1)
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
        m_isSuspended = false;

        DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
        DebugPlugin.getDefault().getExpressionManager().removeExpressionListener(this);

        fireTerminateEvent();

        if (m_processHandle != null)
        {
            try
            {
                terminate();
            }
            catch (DebugException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            m_debugServer.shutdown();
        }
        catch (DebugServerException e)
        {
        }
    }

    @Override
    public void resumed()
    {
        waitDebugProcessing();
        cleanState();
        m_isSuspended = false;
        waitDebugProcessing();
        resumed(DebugEvent.CLIENT_REQUEST);
    }

    void cleanState()
    {
        m_threadHandle.setBreakpoints(null);
        m_threadHandle.setStepping(false);
    }

    @Override
    synchronized public void evaluation(boolean valid, String code, String value)
    {
        ConsoleHelper.getAppConsole().show();
        ConsoleHelper.getAppConsole().getStream().println("start");

        IExpressionManager expManager = DebugPlugin.getDefault().getExpressionManager();

        IExpression[] modelExps = expManager.getExpressions();

        for (IExpression currExp : modelExps)
        {
            if (currExp.getExpressionText().equals(code))
            {
                if (currExp instanceof RhogenWatchExpression)
                {
                    IValue watchVal = new DebugValue(this, value);
                    RhogenWatchExpression watchExp = (RhogenWatchExpression) currExp;
                    watchExp.setResult(new RhogenWatchExpressionResult(code, watchVal));
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
        IExpressionManager m = DebugPlugin.getDefault().getExpressionManager();
        waitDebugProcessing();
        String expText = expression.getExpressionText();

        if (!(expression instanceof RhogenWatchExpression))
        {
            m.removeExpression(expression);
            m.addExpression(new RhogenWatchExpression(expText));
            m_debugServer.debugEvaluate(expText);
        }
    }

    @Override
    public void expressionRemoved(IExpression expression)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void expressionChanged(IExpression expression)
    {
        // waitDebugProcessing();
        // String expText = expression.getExpressionText();
        // expression = new RhogenWatchExpression(expText);
        // m_debugServer.debugEvaluate(expText);
    }

    @Override
    public void watch(DebugVariableType type, String variable, String value)
    {
        // TODO Auto-generated method stub
    }
}
