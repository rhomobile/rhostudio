package rhogenwizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


public class RhodesProjectSupport
{
    public static IProject createProject(String projectName, URI location) 
    {
        Assert.isNotNull(projectName);
        Assert.isTrue(projectName.trim().length() != 0);

        IProject project = createBaseProject(projectName, location);

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
            String path = URIUtil.toPath(projectLocation).toOSString();
            path = path + "//" + projectName;

            IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());

            desc.setLocationURI(URIUtil.toURI(path));
            
            try 
            {
                newProject.create(desc, null);
                
                if (!newProject.isOpen()) {
                    newProject.open(null);
                }
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
        }
        else
        {
        	//TODO - add message box 
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

    private static void createFile(IFile file, String pathToFile) throws CoreException, FileNotFoundException 
    {
    	File nf = new File(pathToFile);
    	
    	if (nf.canRead())
    	{
    		InputStream stream = new FileInputStream(nf);
    		
			if (file.exists()) {
				file.setContents(stream, true, true, null);
			} else {
				file.create(stream, true, null);
			}
    	}
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
    	if (newProject == null)
    		return;

    	try
    	{
	    	File appFodler = new File(projectPath);
	    	
	    	if (appFodler.exists() && appFodler.isDirectory())
	    	{
	    		String[] paths = appFodler.list();
	
	    		for (String path : paths) 
	            {
	    			String endPath = projectPath + "/" + path;
	    			
	        		File currFile = new File(endPath);
	            	
	        		if (currFile.isDirectory())
	        		{
	        			IFolder etcFolders = newProject.getFolder(path);
	                    etcFolders.createLink(URIUtil.toURI(new Path(currFile.getPath())), IResource.ALLOW_MISSING_LOCAL, null);
	        		}
	        		else
	        		{
	                    IFile etcFile = newProject.getFile(currFile.getName());
	                    createFile(etcFile, currFile.getPath());        			
	        		}
	            }
	    	}
    	}
    	catch(FileNotFoundException e)
    	{
    		e.printStackTrace();
    	}
    }
}
