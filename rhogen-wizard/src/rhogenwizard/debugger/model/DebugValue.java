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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

/**
 * Value of a PDA variable.
 */
public class DebugValue extends DebugElement implements IValue 
{	
	private String      m_currValue    = null;
	private boolean     m_hasVariables = false;
	private IVariable[] m_childsVariables = null;
	
	public DebugValue(DebugTarget target, String value, boolean a)
	{
		super(target);
		m_currValue = value;		
	}
	
	public DebugValue(DebugTarget target, String value) 
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
	
	private void parseList(DebugTarget target, String s)
	{
		String prepareValue = s.subSequence(1, s.length() - 1).toString();
		StringTokenizer st = new StringTokenizer(prepareValue, ",");

		m_childsVariables = new DebugVariable[st.countTokens()];
		m_hasVariables    = true;
		
		int idx=0;
		while (st.hasMoreTokens()) 
	    {
			try 
			{				
				Integer intConverter = new Integer(idx);
				m_childsVariables[idx] = new DebugVariable(target, intConverter.toString());
				m_childsVariables[idx].setValue(new DebugValue(target, st.nextToken(), false));				
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
	
	private Map<String, String> findSubObjects(String parentObject)
	{
		Map<String, String> outList = new HashMap<String, String>();
		
		char[] dstBuffer = new char[parentObject.length()];		
		parentObject.getChars(0, parentObject.length(), dstBuffer, 0);
		
		StringBuilder sb = new StringBuilder();
		boolean isObject = false;
		String name = null;
		
		for (int i=0; i<dstBuffer.length; ++i)
		{
			if (dstBuffer[i] == '{') 
			{
				StringBuilder nameBuilder = new StringBuilder();
								
				for (int j=i-3; j>0; j--)
				{
					if (dstBuffer[j] == ',')
						break;
					
					nameBuilder.append(dstBuffer[j]);
				}
					
				name = nameBuilder.reverse().toString();
				
				isObject = true;
				sb       = new StringBuilder();
			}
			else if (dstBuffer[i] == '}') 
			{
				isObject = false;
				sb.append('}');
				
				outList.put(name, sb.toString());
			}
			
			if (isObject)
				sb.append(dstBuffer[i]);			
		}
		
		return outList;
	}
	
	String removeSubObjects(Map<String, String> subObjects, String parentObject)
	{
		for (String objString : subObjects.values())
		{
			parentObject = parentObject.replace(objString, "");
		}

		return parentObject;
	}
	
	List<String> splitSubVariables(String parentObject)
	{
		List<String> out = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(parentObject, ",");
		StringBuilder sb = new StringBuilder() ;  
		
		while (st.hasMoreTokens()) 
	    {
			String tokenString = st.nextToken();
			
			sb.append(tokenString);
			
			char[] dstBuffer = new char[tokenString.length()];		
			tokenString.getChars(0, tokenString.length(), dstBuffer, 0);		
						
			if (dstBuffer[tokenString.length() - 1] == '\"')
			{
				out.add(sb.toString());
				sb = new StringBuilder();
			}
			else
			{
				String s = new String(sb.toString());
				
				String[] ss = s.split("=>");
				
				if (ss.length > 1)
				{
					try
					{
						out.add(sb.toString());
						sb = new StringBuilder();						
					}
					catch (NumberFormatException  e) {
					}
				}
				else 
				{
					out.add(sb.toString());
					sb = new StringBuilder();										
				}
			}
	    }
		
		return out;
	}

	private void parseObject(DebugTarget target, String s)
	{
		String prepareValue = s.subSequence(1, s.length() - 1).toString();
		
		Map<String, String> subObjects = findSubObjects(prepareValue);
		
		if (subObjects.size() != 0)
		{
			prepareValue = removeSubObjects(subObjects, prepareValue);
		}
		
		List<String> splitTokens = splitSubVariables(prepareValue);

		if (splitTokens.size() == 0)
			return;

		m_childsVariables = new DebugVariable[splitTokens.size()];		
		m_hasVariables    = true;
		
		int idx=0;
		for (String string : splitTokens) 
		{
			try 
			{				
				String[] stValueToken = string.split("=>");
				
				if (stValueToken.length > 1)
				{
					m_childsVariables[idx] = new DebugVariable(target, stValueToken[0]);
					m_childsVariables[idx].setValue(new DebugValue(target, stValueToken[1], false));
				}
				else
				{
					String value = subObjects.get(stValueToken[0]);
					
					m_childsVariables[idx] = new DebugVariable(target, stValueToken[0]);
					
					if (value != null)
						m_childsVariables[idx].setValue(new DebugValue(target, value));
					else
						m_childsVariables[idx].setValue(new DebugValue(target, "", false));
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
