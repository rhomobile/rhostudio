package rhogenwizard.sdk.task;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IRunTask extends Runnable
{
    boolean isOk();
    void run(IProgressMonitor monitor);
}