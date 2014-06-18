package rhogenwizard.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import rhogenwizard.BuildType;
import rhogenwizard.DialogUtils;
import rhogenwizard.PlatformType;
import rhogenwizard.builder.rhodes.ConfigProductionBuildDialog;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;
import rhogenwizard.rhohub.TokenChecker;
import rhogenwizard.sdk.task.LocalProductionBuildTask;
import rhogenwizard.sdk.task.RhohubProductionBuildTask;

public class ProductionBuildAction implements IWorkbenchWindowActionDelegate
{
    private IWorkbenchWindow window;

    @Override
    public void run(IAction action)
    {
        IProject project = ProjectFactory.getInstance().getSelectedProject();

        if (project == null)
        {
            DialogUtils.error("Error", "Before run production build select RhoMobile project");
            return;
        }

        if (!RhodesProject.checkNature(project) && !RhoelementsProject.checkNature(project))
        {
            DialogUtils.error("Error", "Production build can run only for RhoMobile project");
            return;
        }

        if (!TokenChecker.processToken(project))
        {
            return;
        }

        ConfigProductionBuildDialog dialog = new ConfigProductionBuildDialog(window.getShell());
        dialog.create();
         
        if (dialog.open() == Window.OK)
        {
            String projectName = "<unknown>";
            try
            {
                projectName = project.getDescription().getName();
            }
            catch (CoreException e)
            {
            }

            PlatformType platformType = dialog.platformType();
            BuildType    buildType    = dialog.buildType   ();
        	
            assert platformType != PlatformType.eUnknown;
            assert buildType    != BuildType   .eUnknown;

            String workDir = project.getLocation().toOSString();
            switch (buildType) {
            case eLocal:
                new LocalProductionBuildTask(workDir, platformType)
                .makeJob("Production build (" + projectName + ")")
                .schedule();
                break;
            case eRhoMobileCom:
                new RhohubProductionBuildTask(workDir, platformType)
                .makeJob("RhoMobile.com production build (" + projectName + ")")
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
