package rhogenwizard.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import rhogenwizard.BuildType;
import rhogenwizard.DialogUtils;
import rhogenwizard.PlatformType;
import rhogenwizard.builder.rhodes.ConfigProductionBuildDialog;
import rhogenwizard.sdk.task.LocalProductionBuildTask;

public class ProductionBuildAction implements IWorkbenchWindowActionDelegate
{
    private IWorkbenchWindow window;

    @Override
    public void run(IAction action)
    {
        if (!ConfigProductionBuildDialog.thereAreRhomobileProjects())
        {
            DialogUtils.error("Error", "There are no RhoMobile projects.");
            return;
        }

        ConfigProductionBuildDialog dialog = new ConfigProductionBuildDialog(window.getShell());
        dialog.create();
         
        if (dialog.open() == Window.OK)
        {
            IProject project = dialog.project();
            PlatformType platformType = dialog.platformType();
            BuildType    buildType    = dialog.buildType   ();
        	
            assert platformType != PlatformType.eUnknown;
            assert buildType    != BuildType   .eUnknown;

            String workDir = project.getLocation().toOSString();
            switch (buildType) {
            case eLocal:
                new LocalProductionBuildTask(workDir, platformType)
                .makeJob("Production build (" + project.getName() + ")")
                .schedule();
                break;
            }
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection)
    {
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void init(IWorkbenchWindow window)
    {
        this.window = window;
    }
}
