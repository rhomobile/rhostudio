package rhogenwizard;

import java.net.URI;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class BuildInfoHolder 
{
	private static final String[] generalAttributesHelpStrings =
	{
          "Run, but do not make any changes. (-p, --pretend)",
		  "Overwrite files that already exist. (-f, --force)",
		  "Skip files that already exist. (-s, --skip)",
		  "Delete files that have previously been generated with this generator. (-d, --delete)",
		  "Do not catch errors (--debug)",
	};

	private static final String[] generalAttributesFlasg =
	{
          "--pretend",
		  "--force",
		  "--skip",
		  "--delete",
		  "--debug",
	};

	public String appName = null;
	public String appDir  = null;
	
	public boolean isPretend = false;
	public boolean isForce   = false;
	public boolean isSkip    = false;
	public boolean isDelete  = false;
	public boolean isDebug   = false;
	public boolean isInDefaultWs = false;
	
	public String generateAttributeString()
	{
		StringBuilder sb = new StringBuilder();
		
		if (isPretend)
			sb.append(" " + generalAttributesFlasg[0]);
		
		if (isForce)
			sb.append(" " + generalAttributesFlasg[1]);
		
		if (isSkip)
			sb.append(" " + generalAttributesFlasg[2]);
		
		if (isDelete)
			sb.append(" " + generalAttributesFlasg[3]);
		
		if (isDebug)
			sb.append(" " + generalAttributesFlasg[4]);
		
		return sb.toString();
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
	
	public static String[] getAttributesStrings()
	{
		return generalAttributesHelpStrings;
	}
}