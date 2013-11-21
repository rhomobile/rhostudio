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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class DebugVariable extends DebugElement implements IVariable 
{
	@Override
	public String toString()
	{
		return "";
	}

	// name & stack frame
	private String 		m_varName    = null;
	private DebugTarget m_stackFrame = null;
	private IValue      m_varValue   = null;
	
	/**
	 * Constructs a variable contained in the given stack frame
	 * with the given name.
	 * 
	 * @param frame owning stack frame
	 * @param name variable name
	 */
	public DebugVariable(DebugTarget frame, String name) 
	{
		super(frame);
		
		m_stackFrame = frame;
		m_varName = name;
		m_varValue = null;
	}
	
	public IValue getValue() throws DebugException 
	{
		return m_varValue;
	}
	
	public String getName() throws DebugException 
	{
		return m_varName;
	}
	
	public String getReferenceTypeName() throws DebugException 
	{
		return "Thing";
	}
	
	public boolean hasValueChanged() throws DebugException 
	{
		return false;
	}
	
	public void setValue(String expression) throws DebugException 
	{
	}
	
	public void setValue(IValue value) throws DebugException 
	{
		m_varValue = value;
	}
	
	public boolean supportsValueModification() 
	{
		return false;
	}
	
	public boolean verifyValue(String expression) throws DebugException 
	{
		return false;
	}

	public boolean verifyValue(IValue value) throws DebugException 
	{
		return false;
	}
	
	/**
	 * Returns the stack frame owning this variable.
	 * 
	 * @return the stack frame owning this variable
	 * @throws DebugException 
	 */
	protected DebugStackFrame getStackFrame() throws DebugException 
	{
		return (DebugStackFrame) m_stackFrame.getStackFrames()[0];
	}
}
