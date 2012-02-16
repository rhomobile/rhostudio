/**
 * 
 */
package rhogenwizard.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import rhogenwizard.project.extension.ProjectNotFoundExtension;

/**
 * @author anton
 *
 */
public interface IRhomobileProject 
{	
	//
	IProject getProject() throws ProjectNotFoundExtension;
	//
	void addNature() throws CoreException, ProjectNotFoundExtension;
	//
	boolean checkProject() throws ProjectNotFoundExtension;
	//
	void linkFile(String projectPath) throws CoreException, ProjectNotFoundExtension;
	//
	void linkFolder(String filePath) throws CoreException, ProjectNotFoundExtension;
	//
	void deleteProjectFiles() throws ProjectNotFoundExtension, CoreException;
	//
	void refreshProject() throws ProjectNotFoundExtension, CoreException;
}
