package rhogenwizard.project;

import java.io.File;
import java.net.URI;
import java.util.Map;

import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentTypeMatcher;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import rhogenwizard.buildfile.AppYmlFile;
import rhogenwizard.project.extension.ProjectNotFoundExtension;
import rhogenwizard.project.nature.RhodesNature;

public class RhodesProject extends RhomobileProject 
{
	public RhodesProject(IProject project)
	{
		m_project = project;
	}
	
	public static boolean checkNature(IProject otherProject) 
	{
		try 
		{
			IProjectNature nature = otherProject.getNature(RhodesNature.natureId);
		
			if (nature != null)
				return true;		
		} 
		catch (CoreException e) 
		{
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean checkProject() throws ProjectNotFoundExtension
	{
		if (m_project == null)
    		throw new ProjectNotFoundExtension("");

		File projectDir = new File(m_project.getLocation().toOSString());
		
		if (!projectDir.isDirectory() || !projectDir.exists())
		{
			return false;
		}

		File buildFile = new File(projectDir.getAbsolutePath() + File.separator + AppYmlFile.configFileName);
		
		if (!buildFile.exists())
		{
			return false;
		}
		
		return true;
	}

	@Override
	public void addNature() throws CoreException, ProjectNotFoundExtension 
	{
		IProjectDescription description = getProject().getDescription();
		String[] natures = description.getNatureIds();

		// Add the nature
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = RhodesNature.natureId;
		description.setNatureIds(newNatures);
		getProject().setDescription(description, null);
	}
}
