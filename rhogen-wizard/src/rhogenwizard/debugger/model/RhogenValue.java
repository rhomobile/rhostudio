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

import java.util.StringTokenizer;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

/**
 * Value of a PDA variable.
 */
public class RhogenValue extends RhogenDebugElement implements IValue 
{	
	private String  m_currValue    = null;
	private boolean m_hasVariables = false;
	private IVariable[] m_childsVariables = null;
	
	public RhogenValue(RhogenDebugTarget target, String value, boolean a)
	{
		super(target);
		m_currValue = value;		
	}
	
	public RhogenValue(RhogenDebugTarget target, String value) 
	{
		super(target);
		m_currValue = value;
		
		String trimValue = value.replaceAll(" ", "");
		
		if (trimValue.startsWith("{"))
		{
			parseObject(target, trimValue);
		}		
		else if (trimValue.startsWith("["))
		{
			parseList(target, trimValue);
		}
	}
	
	private void parseList(RhogenDebugTarget target, String s)
	{
		String prepareValue = s.subSequence(1, s.length() - 1).toString();
		StringTokenizer st = new StringTokenizer(prepareValue, ",");

		m_childsVariables = new RhogenVariable[st.countTokens()];
		m_hasVariables    = true;
		
		int idx=0;
		while (st.hasMoreTokens()) 
	    {
			try 
			{				
				Integer intConverter = new Integer(idx);
				m_childsVariables[idx] = new RhogenVariable(target, intConverter.toString());
				m_childsVariables[idx].setValue(new RhogenValue(target, st.nextToken(), false));				
			}
			catch (DebugException e) 
			{
				m_childsVariables = null;
				m_hasVariables    = false;
			    e.printStackTrace();
			}
			
			idx++;
	    }
	}
	
	private void parseObject(RhogenDebugTarget target, String s)
	{
		String prepareValue = s.subSequence(1, s.length() - 1).toString();
		StringTokenizer st = new StringTokenizer(prepareValue, ",");
		
		if (st.countTokens() < 1)
			return;
		
		m_childsVariables = new RhogenVariable[st.countTokens()];
		m_hasVariables  = true;
		
		int idx=0;
		while (st.hasMoreTokens()) 
	    {
			try 
			{				
				String[] stValueToken = st.nextToken().split("=>");
				
				if (stValueToken.length > 1)
				{
					m_childsVariables[idx] = new RhogenVariable(target, stValueToken[0]);
					m_childsVariables[idx].setValue(new RhogenValue(target, stValueToken[1], false));
				}
				else
				{
					m_childsVariables[idx] = new RhogenVariable(target, stValueToken[0]);
					m_childsVariables[idx].setValue(new RhogenValue(target, "null", false));
				}
			}
			catch (DebugException e) 
			{
				m_childsVariables = null;
				m_hasVariables    = false;
			    e.printStackTrace();
			}
			
			idx++;
	    }
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getReferenceTypeName()
	 */
	public String getReferenceTypeName() throws DebugException 
	{
		try 
		{
			Integer.parseInt(m_currValue);
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
		return m_currValue;
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
		if (m_hasVariables)
			return m_childsVariables;
		else
			return new IVariable[0];
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#hasVariables()
	 */
	public boolean hasVariables() throws DebugException 
	{
		return m_hasVariables;
	}
	
	public void setValue(String newValue)
	{
		m_currValue = newValue;
	}
}
