package rhogenwizard.project;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import rhogenwizard.project.extension.ProjectNotFoundExtension;

public abstract class RhomobileProject implements IRhomobileProject 
{
	protected IProject m_project = null;
	
	@Override
	public IProject getProject() throws ProjectNotFoundExtension 
	{
		if (m_project == null)
			throw new ProjectNotFoundExtension("");
		
		return m_project;
	}
	
	@Override
	public void linkFile(final String filePath) throws CoreException, ProjectNotFoundExtension 
	{
		if (m_project == null)
    		throw new ProjectNotFoundExtension("");

		IFile etcFile = m_project.getFile(filePath);        
		etcFile.createLink(etcFile.getFullPath(), IResource.ALLOW_MISSING_LOCAL, null);
		m_project.refreshLocal(IResource.DEPTH_INFINITE, null);
	}

	@Override
	public void linkFolder(final String filePath) throws CoreException, ProjectNotFoundExtension  
	{
		if (m_project == null)
    		throw new ProjectNotFoundExtension("");

		IFolder etcFolder = m_project.getFolder(filePath);        
		etcFolder.createLink(etcFolder.getFullPath(), IResource.ALLOW_MISSING_LOCAL, null);
		m_project.refreshLocal(IResource.DEPTH_INFINITE, null);
	}
	
	@Override
	public void deleteProjectFiles() throws ProjectNotFoundExtension, CoreException 
	{
		if (m_project == null)
    		throw new ProjectNotFoundExtension("");

		m_project.delete(false, false, null);
	}
	
	@Override
	public void refreshProject() throws ProjectNotFoundExtension, CoreException
	{
		if (m_project == null)
    		throw new ProjectNotFoundExtension("");

		m_project.refreshLocal(IResource.DEPTH_INFINITE, null);		
	}
}
