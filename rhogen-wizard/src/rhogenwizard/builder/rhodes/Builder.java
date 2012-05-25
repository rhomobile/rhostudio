package rhogenwizard.builder.rhodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import rhogenwizard.ConsoleHelper;
import rhogenwizard.PlatformType;
import rhogenwizard.sdk.facade.RhoTaskHolder;
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

    protected void fullBuild(final IProgressMonitor monitor) throws CoreException
    {
        try
        {
            List<String> out = compileRubyPart();
            getProject().accept(new ResourceVisitor(out));
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
    }

    private List<String> compileRubyPart()
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CompileRubyPartTask.workDir, getProject().getLocation().toOSString());

        Map<String, ?> res =
            RhoTaskHolder.getInstance().runTask(CompileRubyPartTask.class, params);

        List<String> errLines = (List<String>) res.get(CompileRubyPartTask.outStrings);

        return errLines;
    }
}
