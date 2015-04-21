package rhogenwizard.project;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import rhogenwizard.project.extension.ProjectNotFoundException;

public abstract class RhomobileProject implements IRhomobileProject 
{
	protected IProject m_project = null;
	
	@Override
	public IProject getProject() throws ProjectNotFoundException 
	{
		if (m_project == null)
			throw new ProjectNotFoundException("");
		
		return m_project;
	}
	
	@Override
	public void linkFile(final String fileLinkName, final String filePath) throws CoreException, ProjectNotFoundException 
	{
		if (m_project == null)
    		throw new ProjectNotFoundException("");

		IFile etcFile = m_project.getFile(fileLinkName);        
		etcFile.createLink(new Path(filePath), IResource.ALLOW_MISSING_LOCAL, null);
		m_project.refreshLocal(IResource.DEPTH_INFINITE, null);
	}

	@Override
	public void linkFolder(final String folderLinkName, final String filePath) throws CoreException, ProjectNotFoundException  
	{
		if (m_project == null)
    		throw new ProjectNotFoundException("");
		
		
		IFolder etcFolder = m_project.getFolder(folderLinkName);
		etcFolder.delete(false, new NullProgressMonitor());
		etcFolder.createLink(new Path(filePath), IResource.ALLOW_MISSING_LOCAL, new NullProgressMonitor());
		m_project.refreshLocal(IResource.DEPTH_INFINITE, null);
	}
	
	@Override
	public void deleteProjectFiles() throws ProjectNotFoundException, CoreException 
	{
		if (m_project == null)
    		throw new ProjectNotFoundException("");

		m_project.delete(false, false, null);
	}
	
	private void createLiveUpdateLink() throws CoreException
	{
		IFile liveUpdateFile = m_project.getFile("Live update setting");		
	    byte[] bytes = "live update fake contents".getBytes();
	    InputStream source = new ByteArrayInputStream(bytes);
	    liveUpdateFile.create(source, IResource.NONE, null);

	}
	
	@Override
	public void refreshProject() throws ProjectNotFoundException, CoreException
	{
		if (m_project == null)
    		throw new ProjectNotFoundException("");

		createLiveUpdateLink();
		
		m_project.refreshLocal(IResource.DEPTH_INFINITE, null);		
	}
}
