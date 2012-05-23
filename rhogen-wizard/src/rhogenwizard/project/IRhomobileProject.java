/**
 * 
 */
package rhogenwizard.project;

import java.io.FileNotFoundException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import rhogenwizard.buildfile.YmlFile;
import rhogenwizard.project.extension.ProjectNotFoundException;

/**
 * @author anton
 *
 */
public interface IRhomobileProject 
{	
	//
	IProject getProject() throws ProjectNotFoundException;
	//
	void addNature() throws CoreException, ProjectNotFoundException;
	//
	boolean checkProject() throws ProjectNotFoundException;
	//
	public void linkFile(final String fileLinkName, final String filePath) throws CoreException, ProjectNotFoundException;
	//
	public void linkFolder(final String folderLinkName, final String filePath) throws CoreException, ProjectNotFoundException;
	//
	void deleteProjectFiles() throws ProjectNotFoundException, CoreException;
	//
	void refreshProject() throws ProjectNotFoundException, CoreException;
	//
	YmlFile getSettingFile() throws ProjectNotFoundException, FileNotFoundException;
}
