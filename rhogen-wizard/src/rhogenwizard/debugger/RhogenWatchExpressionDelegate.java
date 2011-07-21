package rhogenwizard.debugger;

import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IWatchExpressionDelegate;
import org.eclipse.debug.core.model.IWatchExpressionListener;

import rhogenwizard.debugger.model.RhogenDebugTarget;

public class RhogenWatchExpressionDelegate implements IWatchExpressionDelegate 
{
	@Override
	public void evaluateExpression(String expression, IDebugElement context, IWatchExpressionListener listener) 
	{
		// TODO Auto-generated method stub
		if (context instanceof RhogenDebugTarget)
		{
			
		}
	}

}
