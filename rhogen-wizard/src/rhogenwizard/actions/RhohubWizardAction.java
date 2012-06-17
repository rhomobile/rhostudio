package rhogenwizard.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import rhogenwizard.DialogUtils;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;
import rhogenwizard.wizards.rhohub.BuildWizard;

public class RhohubWizardAction implements IWorkbenchWindowActionDelegate
{
    private IWorkbenchWindow window;

    /**
     * The action has been activated. The argument of the method represents the
     * 'real' action sitting in the workbench UI.
     * 
     * @see IWorkbenchWindowActionDelegate#run
     */
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

        BuildWizard w =  new BuildWizard(project);
        WizardDialog wd = new WizardDialog(window.getShell(), w);
        wd.create();
        wd.open();
        
//        String s = null;
//        Iterable<RevCommit> logC = null;
//        try
//        {
//            Git g = Git.open(new File("C:\\Android\\rhodes-system-api-samples"));
//            logC = g.log().call(); 
//            s =logC.toString();
//        }
//        catch (IOException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        catch (NoHeadException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        catch (JGitInternalException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    /**
     * Selection in the workbench has been changed. We can change the state of
     * the 'real' action here if we want, but this can only happen after the
     * delegate has been created.
     * 
     * @see IWorkbenchWindowActionDelegate#selectionChanged
     */
    public void selectionChanged(IAction action, ISelection selection)
    {
    }

    /**
     * We can use this method to dispose of any system resources we previously
     * allocated.
     * 
     * @see IWorkbenchWindowActionDelegate#dispose
     */
    public void dispose()
    {
    }

    /**
     * We will cache window object in order to be able to provide parent shell
     * for the message dialog.
     * 
     * @see IWorkbenchWindowActionDelegate#init
     */
    public void init(IWorkbenchWindow window)
    {
        this.window = window;
    }
}
