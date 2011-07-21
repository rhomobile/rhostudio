package rhogenwizard.debugger;

import org.eclipse.debug.core.model.IWatchExpressionResult;
import org.eclipse.debug.internal.core.WatchExpression;

public class RhogenWatchExpression extends WatchExpression
{
	public RhogenWatchExpression(String expression) 
	{
		super(expression);
	}

	public void evaluate() 
	{
		//setPending(true);
		//setResult(this.fResult);
	}

	@Override
	public void setResult(IWatchExpressionResult result) 
	{
		
		super.setResult(result);
	}
}
