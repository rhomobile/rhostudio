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

/**
 * Value of a PDA variable.
 */
public class RhogenValue extends RhogenDebugElement implements IValue 
{	
	private String fValue;
	
	public RhogenValue(RhogenDebugTarget target, String value) 
	{
		super(target);
		fValue = value;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getReferenceTypeName()
	 */
	public String getReferenceTypeName() throws DebugException 
	{
		try 
		{
			Integer.parseInt(fValue);
		} 
		catch (NumberFormatException e) 
		{
			return "text";
		}
		
		return "integer";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getValueString()
	 */
	public String getValueString() throws DebugException 
	{
		return fValue;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#isAllocated()
	 */
	public boolean isAllocated() throws DebugException 
	{
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getVariables()
	 */
	public IVariable[] getVariables() throws DebugException 
	{
		return new IVariable[0];
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#hasVariables()
	 */
	public boolean hasVariables() throws DebugException 
	{
		return false;
	}
	
	public void setValue(String newValue)
	{
		fValue = newValue;
	}
}
