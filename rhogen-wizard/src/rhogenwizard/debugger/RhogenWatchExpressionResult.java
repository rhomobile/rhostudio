package rhogenwizard.debugger;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IWatchExpressionResult;

public class RhogenWatchExpressionResult implements IWatchExpressionResult 
{
	IValue m_watchValue = null;
	String m_expText = null;
	
	public RhogenWatchExpressionResult(String expText, IValue watchValue) 
	{
		m_watchValue = watchValue;
		m_expText    = expText;
	}
	
	@Override
	public IValue getValue() 
	{
		return m_watchValue;
	}

	@Override
	public boolean hasErrors() 
	{
		return (m_watchValue == null);
	}

	@Override
	public String[] getErrorMessages() 
	{
		return null;
	}

	@Override
	public String getExpressionText() 
	{
		return m_expText;
	}

	@Override
	public DebugException getException() 
	{
		return null;
	}
}
