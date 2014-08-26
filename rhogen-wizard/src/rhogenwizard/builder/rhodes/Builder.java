package rhogenwizard.builder.rhodes;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import rhogenwizard.Activator;
import rhogenwizard.PlatformType;
import rhogenwizard.rhohub.TokenChecker;
import rhogenwizard.sdk.task.CleanCloudCacheTask;
import rhogenwizard.sdk.task.CleanPlatformTask;
import rhogenwizard.sdk.task.CompileRubyPartTask;
import rhogenwizard.sdk.task.IRunTask;
import rhogenwizard.sdk.task.RunTask.StoppedException;

public class Builder extends IncrementalProjectBuilder
{
    public static final String BUILDER_ID = "rhogenwizard.builder.RhogenBuilder";

    @Override
    protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args,
        final IProgressMonitor monitor) throws CoreException
    {
        // need implement separate rake command for build ruby part without
        // extension and other staff build
        // fullBuild(monitor);

        return null;
    }

    @Override
    protected void clean(IProgressMonitor monitor) throws CoreException
    {
		if (!TokenChecker.processToken(getProject()))
			return;
    	
        for (PlatformType platformType : PlatformType.values())
        {
            switch (platformType)
            {
            case eRsync:
            case eUnknown:
                continue;
            }
             
            run_silent(monitor, new CleanPlatformTask(
                getProject().getLocation().toOSString(), platformType));
        }

        run_silent(monitor, new CleanCloudCacheTask(getProject().getLocation().toOSString()));

        super.clean(monitor);
    }

    protected void fullBuild(final IProgressMonitor monitor)
    {
        try
        {
            CompileRubyPartTask task = new CompileRubyPartTask(
                getProject().getLocation().toOSString());
            task.run(monitor);
            List<String> out = task.getOutputStrings();
            getProject().accept(new ResourceVisitor(out));
        }
        catch (CoreException e)
        {
            Activator.logError(e);
        }
    }

    private static void run_silent(IProgressMonitor monitor, IRunTask task)
    {
        try {
            task.run(monitor);
        } catch (StoppedException e) {
        }
    }
}
