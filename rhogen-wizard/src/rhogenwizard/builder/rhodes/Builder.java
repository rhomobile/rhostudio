package rhogenwizard.builder.rhodes;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import rhogenwizard.Activator;
import rhogenwizard.ConsoleHelper;
import rhogenwizard.PlatformType;
import rhogenwizard.sdk.task.CleanPlatformTask;
import rhogenwizard.sdk.task.CompileRubyPartTask;
import rhogenwizard.sdk.task.RakeTask;

public class Builder extends IncrementalProjectBuilder
{
    public static final String BUILDER_ID = "rhogenwizard.builder.RhogenBuilder";

    public Builder()
    {
        super();
    }

    protected IProject[] build(int kind, Map args, final IProgressMonitor monitor)
        throws CoreException
    {
        // need implement separate rake command for build ruby part without
        // extension and other staff build
        // fullBuild(monitor);

        return null;
    }

    @Override
    protected void clean(IProgressMonitor monitor) throws CoreException
    {
        try
        {
            ConsoleHelper.consoleBuildPrint("Clean project started");

            PlatformType platformTypes[] =
                {
                    PlatformType.eAndroid, PlatformType.eBb, PlatformType.eIPhone,
                    PlatformType.eWm, PlatformType.eWp7 };

            for (PlatformType platformType : platformTypes)
            {
                RakeTask task =
                    new CleanPlatformTask(getProject().getLocation().toOSString(), platformType);
                task.run(monitor);
            }

            ConsoleHelper.consoleBuildPrint("Clean application cancelled");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        super.clean(monitor);
    }

    protected void fullBuild(final IProgressMonitor monitor)
    {
        try
        {
            RakeTask task = new CompileRubyPartTask(getProject().getLocation().toOSString());
            Map<String, ?> res = task.run(monitor);
            List<String> out = (List<String>) res.get(CompileRubyPartTask.outStrings);
            getProject().accept(new ResourceVisitor(out));
        }
        catch (CoreException e)
        {
            Activator.logError(e);
        }
    }
}
