package rhogenwizard.sdk.task;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IRunTask
{
    boolean isOk();
    void run();
    void run(IProgressMonitor monitor);
}
