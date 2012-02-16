package rhogenwizard;

import java.io.File;
import java.net.URI;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class BuildInfoHolder 
{
	public String  appName = null;
	public String  appDir  = null;
	public boolean existCreate = false; 	
	public boolean isInDefaultWs = false;
	
	public String getProjectLocationFullPath()
	{
		if (isInDefaultWs)
		{
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IPath location = root.getLocation();
						
			return location.toOSString() + File.separator + appName;
		}
		
		return appDir + File.separator + appName;
	}
	
	public IPath getProjectLocationPath()
	{
		if (isInDefaultWs)
		{
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IPath location = root.getLocation();
			
			return location;
		}
		
		return new Path(appDir);
	}
	
	public URI getProjectLocation()
	{
		return URIUtil.toURI(getProjectLocationPath());
	}	
}