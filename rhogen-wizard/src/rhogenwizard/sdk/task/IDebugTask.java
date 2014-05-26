package rhogenwizard.sdk.task;

import org.eclipse.debug.core.model.IProcess;

public interface IDebugTask extends IRunTask
{
    IProcess getDebugProcess();
}
