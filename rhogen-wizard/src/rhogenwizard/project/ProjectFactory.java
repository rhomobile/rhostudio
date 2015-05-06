package rhogenwizard.project;

import java.io.File;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import rhogenwizard.BuildInfoHolder;
import rhogenwizard.project.extension.AlredyCreatedException;
import rhogenwizard.project.extension.BadProjectTagException;
import rhogenwizard.project.extension.ProjectNotFoundException;

public class ProjectFactory implements IProjectFactory
{
	private static ProjectFactory factoryInstance = null;
	
	public static IProjectFactory getInstance()
	{
		if (factoryInstance == null)
			factoryInstance = new ProjectFactory();
		
		return (IProjectFactory) factoryInstance;
	}
	
	public IPath getWorkspaceDir()
	{
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IPath location = root.getLocation();
      
        return location;    
	}
	
    /**
     * Just do the basics: create a basic project.
     *
     * @param location
     * @param projectName
     * @throws AlredyCreatedException 
     * @throws CoreException 
     */
    private IProject createBaseProject(BuildInfoHolder projectInfo) throws AlredyCreatedException, CoreException 
    {
        // it is acceptable to use the ResourcesPlugin class
        IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectInfo.appName);
        if (newProject.exists())
        {
            throw new AlredyCreatedException(newProject);
        }

        IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());

        if (!projectInfo.isInDefaultWs)
        {
            if (projectInfo.existCreate && !isProjectLocationInWorkspace(projectInfo.appDir))
            {
                desc.setLocation(projectInfo.getAppDirPath());
            }
            else if (!projectInfo.existCreate)
            {
                desc.setLocation(projectInfo.getAppDirPath());
            }
        }

        newProject.create(desc, new NullProgressMonitor());

        if (!newProject.isOpen())
        {
            newProject.open(new NullProgressMonitor());
        }

        return newProject;
    }

    private IRhomobileProject createRhomobileProject(Class<? extends IRhomobileProject> projectTag, IProject project) throws BadProjectTagException
    {
    	if (projectTag.equals(RhodesProject.class))
    	{
    		return new RhodesProject(project);
    	}
    	else if (projectTag.equals(RhoconnectProject.class))
    	{
    		return new RhoconnectProject(project);
    	}
    	else if (projectTag.equals(RhoelementsProject.class))
    	{
    		return new RhoelementsProject(project);
    	}
    	
    	throw new BadProjectTagException(projectTag);
    }
    
    public boolean isProjectLocationInWorkspace(final String projectPath)
    {
		String wsPath = getWorkspaceDir().toOSString();
		
		return projectPath.toLowerCase().contains(wsPath.toLowerCase()); 
    }
    
	public IRhomobileProject createProject(Class<? extends IRhomobileProject> projectTag, BuildInfoHolder projectInfo) 
		throws CoreException, ProjectNotFoundException, AlredyCreatedException, BadProjectTagException
	{
        Assert.isNotNull(projectInfo.appName);
        Assert.isTrue(projectInfo.appName.trim().length() != 0);

        IProject project = createBaseProject(projectInfo);

        IRhomobileProject rhoProject = createRhomobileProject(projectTag, project);
        
        rhoProject.addNature();
        
        return rhoProject;
	}

	@Override
	public IProject getSelectedProject() 
	{
		IProject project = null;
		
		IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
		
		if (workbenchWindows.length > 0)
		{
			IWorkbenchPage page = workbenchWindows[0].getActivePage(); 
		
			ISelection selection = page.getSelection();
	
			if (selection instanceof IStructuredSelection)
			{
				IStructuredSelection sel = (IStructuredSelection) selection;
				Object res = sel.getFirstElement();
				
				if (res instanceof IResource)
				{
				   project = ((IResource)res).getProject();
				}		
			}
		}
		
		return project;	
	}

	@Override
	public IRhomobileProject convertFromProject(IProject project) throws BadProjectTagException 
	{
		if (RhodesProject.checkNature(project))
		{
			return createRhomobileProject(RhodesProject.class, project);
		}
		else if (RhoconnectProject.checkNature(project))
		{
			return createRhomobileProject(RhoconnectProject.class, project);
		}
    	else if (RhoelementsProject.checkNature(project))
    	{
    		return createRhomobileProject(RhoelementsProject.class, project);
    	}

		throw new BadProjectTagException(IProject.class);
	}
	
	@Override
	public Class<?> typeFromProject(IProject project) throws BadProjectTagException
	{
		if (RhodesProject.checkNature(project))
		{
			return RhodesProject.class;
		}
		else if (RhoconnectProject.checkNature(project))
		{
			return RhoconnectProject.class;
		}
    	else if (RhoelementsProject.checkNature(project))
    	{
    		return RhoelementsProject.class;
    	}

		throw new BadProjectTagException(IProject.class);
	}
}
