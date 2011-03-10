package rhogenwizard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;


public class CustomProjectSupport {
    /**
     * For this marvelous project we need to:
     * - create the default Eclipse project
     * - add the custom project nature
     * - create the folder structure
     *
     * @param projectName
     * @param location
     * @param natureId
     * @return
     */
    public static IProject createProject(String projectName, URI location) 
    {
        Assert.isNotNull(projectName);
        Assert.isTrue(projectName.trim().length() != 0);

        IProject project = createBaseProject(projectName, location);
        
        try 
        {
            String[] paths = { "parent/child1-1/child2", "parent/child1-2/child2/child3"/*, location.getPath().toString() + "/" 
            		+ projectName + "/build.yml"*/}; //$NON-NLS-1$ //$NON-NLS-2$
            //addFolderToProjectStructure(project, paths);
            
            addFilesToProjectStructure(project, location.getPath() + "/" + projectName);
        } 
        catch (CoreException e) 
        {
            e.printStackTrace();
            project = null;
        }

        return project;
    }

    /**
     * Just do the basics: create a basic project.
     *
     * @param location
     * @param projectName
     */
    private static IProject createBaseProject(String projectName, URI location) 
    {
        // it is acceptable to use the ResourcesPlugin class
        IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

        if (!newProject.exists())
        {
            URI projectLocation = location;
            IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());
            
            if (location != null || ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location)) {
                projectLocation = null;
            }

            desc.setLocationURI(projectLocation);
            
            try 
            {
                newProject.create(desc, null);
                if (!newProject.isOpen()) {
                    newProject.open(null);
                }
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }

        return newProject;
    }

    private static void createFolder(IFolder folder) throws CoreException
    {
        IContainer parent = folder.getParent();
        
        if (parent instanceof IFolder) {
            createFolder((IFolder) parent);
        }
        
        if (!folder.exists()) {
            folder.create(false, true, null);
        }
    }

    private static void createFile(IFile file, String pathToFile) throws CoreException 
    {
    	file.createLink(URIUtil.toURI(new Path(pathToFile)), IResource.ALLOW_MISSING_LOCAL, null);
    }
    
    /**
     * Create a folder structure with a parent root, overlay, and a few child
     * folders.
     *
     * @param newProject
     * @param paths
     * @throws CoreException
     */
    private static void addFolderToProjectStructure(IProject newProject, String[] paths) throws CoreException
    {
        for (String path : paths) {
            IFolder etcFolders = newProject.getFolder(path);
            createFolder(etcFolders);
        }
    }

    private static void addFilesToProjectStructure(IProject newProject, String projectPath) throws CoreException
    {
    	File appFodler = new File(projectPath);
    	
    	if (appFodler.exists() && appFodler.isDirectory())
    	{
    		String[] paths = appFodler.list();

    		for (String path : paths) 
            {
        		File currFile = new File(projectPath + "/" + path);
            	
        		if (currFile.isDirectory())
        		{
                    IFolder etcFolders = newProject.getFolder(path);
                    createFolder(etcFolders);
                    
                    //addFilesToProjectStructure(newProject, currFile.getPath());
        		}
        		else
        		{
                    IFile etcFile = newProject.getFile(currFile.getName());
                    createFile(etcFile, currFile.getPath());        			
        		}
            }
    	}
    }

}
