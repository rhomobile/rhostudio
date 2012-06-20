package rhogenwizard.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.osgi.service.prefs.BackingStoreException;

import rhogenwizard.DialogUtils;
import rhogenwizard.project.ProjectFactory;
import rhogenwizard.project.RhodesProject;
import rhogenwizard.project.RhoelementsProject;
import rhogenwizard.rhohub.IRhoHubSetting;
import rhogenwizard.rhohub.IRhoHubSettingSaver;
import rhogenwizard.rhohub.RhoHub;
import rhogenwizard.rhohub.RhoHubBundleSetting;
import rhogenwizard.wizards.rhohub.BuildWizard;
import rhogenwizard.wizards.rhohub.LinkWizard;


//InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), "Test", "Please input text.",
//    "Test-Text", null) {
//
//  /**
//   * Override this method to make the text field multilined
//   * and give it a scroll bar. But...
//   */
//  @Override
//  protected int getInputTextStyle() {
//    return SWT.SINGLE | SWT.BORDER ;
//  }
//
////  /**
////   * ...it still is just one line high.
////   * This hack is not very nice, but at least it gets the job done... ;o)
////   */
////  @Override
////  protected Control createDialogArea(Composite parent) {
////    Control res = super.createDialogArea(parent);
////    ((GridData) this.getText().getLayoutData()).heightHint = 10;
////    return res;
////  }
//};
//dlg.open();

public class RhohubWizardAction implements IWorkbenchWindowActionDelegate
{
    private static int wizardHeigth = 500;
    private static int wizardWidth  = 800;
        
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
            DialogUtils.error("Error", "Before run remote build select RhoMobile project's");
            return;
        }

        if (!RhodesProject.checkNature(project) && !RhoelementsProject.checkNature(project))
        {
            DialogUtils.error("Error", "Remote build can run only for RhoMobile project's");
            return;
        }
        
        IRhoHubSetting setting = new RhoHubBundleSetting(project);
                
        if (!checkProjectProperties(project))
        {
            if (DialogUtils.confirm("Project setting", "For project " + project.getName() + 
                    " not found infomation on RhoHub. Link the project with project on RhoHub server?"))
            {
                LinkWizard linkWizard = new LinkWizard(project);
                createWizardDialog(linkWizard);
            }
            else
            {
                return;
            }
        }

        BuildWizard  buildWizard =  new BuildWizard(project);
        createWizardDialog(buildWizard);
    }
    
    void createWizardDialog(IWizard wizard)
    {
        WizardDialog buildWizardDialog = new WizardDialog(window.getShell(), wizard) 
        {
            @Override
            protected void configureShell(Shell newShell) 
            {
                super.configureShell(newShell);
                newShell.setSize(wizardWidth, wizardHeigth);
            }       
        };
        
        buildWizardDialog.create();
        buildWizardDialog.open(); 
    }

    private boolean checkProjectProperties(IProject project)
    {
        IRhoHubSetting setting = new RhoHubBundleSetting(project);
        
        if (!setting.isLinking())
        {
            if (RhoHub.getInstance(setting).isRemoteProjectExist(project))
            {
                try
                {
                    IRhoHubSettingSaver settingSave = (IRhoHubSettingSaver)setting;
                    settingSave.setLinking();
                    return true;
                }
                catch (BackingStoreException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        return setting.isLinking();
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
