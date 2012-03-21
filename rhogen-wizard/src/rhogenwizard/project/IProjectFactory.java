package rhogenwizard.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import rhogenwizard.BuildInfoHolder;
import rhogenwizard.project.extension.AlredyCreatedException;
import rhogenwizard.project.extension.BadProjectTagException;
import rhogenwizard.project.extension.ProjectNotFoundException;

public interface IProjectFactory 
{
	//
	IRhomobileProject createProject(Class<? extends IRhomobileProject> projectTag, BuildInfoHolder projectInfo) throws CoreException, AlredyCreatedException, BadProjectTagException, ProjectNotFoundException;
	//
	IRhomobileProject convertFromProject(IProject project) throws BadProjectTagException;
	//
	IProject getSelectedProject();
	//
	boolean isProjectLocationInWorkspace(final String projectPath);
}
