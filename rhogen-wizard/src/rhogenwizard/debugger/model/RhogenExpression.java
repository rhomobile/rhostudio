package rhogenwizard.debugger.model;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IExpression;
import org.eclipse.debug.core.model.IValue;

import rhogenwizard.debugger.RhogenConstants;

public class RhogenExpression implements IExpression
{
	IDebugTarget m_target;
	ILaunch      m_launch;
	String       m_expName;
	IValue       m_expValue;
	
	RhogenExpression(IDebugTarget target, ILaunch launch, String name, IValue value)
	{
		m_target = target;
		m_launch = launch;
		m_expName  = name;
		m_expValue = value;
	}

	@Override
	public String getModelIdentifier() 
	{
		return RhogenConstants.debugModelId;
	}

	@Override
	public ILaunch getLaunch() 
	{
		return m_launch;
	}

	@Override
	public Object getAdapter(Class adapter) 
	{
		return null;
	}

	@Override
	public String getExpressionText() 
	{
		return m_expName;
	}

	@Override
	public IValue getValue() 
	{
		return m_expValue;
	}

	@Override
	public IDebugTarget getDebugTarget() 
	{
		return m_target;
	}

	@Override
	public void dispose()
	{
	}

	public void setValue(IValue newVal)
	{
		m_expValue = newVal;
	}
}
