package rhogenwizard.wizards;

import java.io.File;
import java.util.Arrays;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public abstract class BaseAppWizard extends Wizard implements INewWizard
{
    protected static String repoPathTag = "Repository";
    
    protected IStructuredSelection m_selection = null;

    public BaseAppWizard()
    {
        super();
        setNeedsProgressMonitor(true);
    }

    public boolean isSelected()
    {
        return !m_selection.isEmpty();
    }
    
    public String getProjectNameFromGitRepo()
    {
        String pathToApp = getProjectPathFromGitRepo();
        
        if (pathToApp == null)
            return null;
        
        String[] pathAppSegs = pathToApp.split("\\\\");
        
        if (pathAppSegs.length < 2)
        	return "";
        
        return pathAppSegs[pathAppSegs.length - 1].trim();
    }
    
    public String getProjectPathFromGitRepo()
    {
        if (m_selection == null)
            return null;
        
        if (m_selection.isEmpty())
            return null;
        
        IAdaptable firstElement = (IAdaptable) m_selection.getFirstElement();
        String pathToRepo = firstElement.toString();
        
        String[] pathToRepoArray = pathToRepo.split(repoPathTag);
        String pathToApp = null;
        
        if (pathToRepoArray.length != 3)
            return null;
        
        pathToApp = pathToRepoArray[2];
        pathToApp = pathToApp.replaceAll("\\[", " ");
        pathToApp = pathToApp.replaceAll("\\]", " ");
        pathToApp = pathToApp.replaceAll("\\\\", "/");
        pathToApp.trim();
        
        String[] pathAppSegs = pathToApp.split("/");
        
        String[] newPathArray = Arrays.copyOfRange(pathAppSegs, 0, pathAppSegs.length - 1, String[].class);
        
        StringBuilder sb = new StringBuilder();
        
        for (String it : newPathArray)
        {
            sb.append(it + File.separator);
        }
        
        return sb.toString().trim();
    }
    
    /**
     * We will accept the selection in the workbench to see if we can initialize
     * from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        this.m_selection = selection;
    }
}