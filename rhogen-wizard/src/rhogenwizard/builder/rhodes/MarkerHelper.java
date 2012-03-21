package rhogenwizard.builder.rhodes;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class MarkerHelper 
{
	public void addMarker(IFile file, String message, int lineNumber, int severity) 
	{
		try 
		{
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			
			if (lineNumber == -1) 
			{
				lineNumber = 1;
			}
			
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		}
		catch (CoreException e) 
		{
		}
	}
	
	public void deleteMarkers(IFile file) 
	{
		try 
		{
			file.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
		}
		catch (CoreException ce) 
		{
		}
	}
}
