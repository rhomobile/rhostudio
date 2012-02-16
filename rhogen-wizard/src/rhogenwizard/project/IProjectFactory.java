package rhogenwizard.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import rhogenwizard.BuildInfoHolder;
import rhogenwizard.project.extension.AlredyCreatedException;
import rhogenwizard.project.extension.BadProjectTagException;
import rhogenwizard.project.extension.ProjectNotFoundExtension;

public interface IProjectFactory 
{
	//
	IRhomobileProject createProject(Class projectTag, BuildInfoHolder projectInfo) throws CoreException, ProjectNotFoundExtension, AlredyCreatedException, BadProjectTagException;
	//
	IProject getSelectedProject();
	//
	boolean isProjectLocationInWorkspace(final String projectPath);
}
