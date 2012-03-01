package rhogenwizard.builder.rhodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

class ResourceVisitor implements IResourceVisitor 
{
	private static String compileMarker = "Running compileRB";
	
	List<String> m_compileOutput = null;
	
	public ResourceVisitor(List<String> compileOutput)
	{
		m_compileOutput = compileOutput;
	}
	
	public boolean visit(IResource resource) 
	{	
		if (resource instanceof IFile && resource.getName().endsWith(".rb")) 
		{
			IFile file = (IFile) resource;
			deleteMarkers(file);		

			try {
				resource.refreshLocal(IResource.DEPTH_ONE, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (m_compileOutput == null || m_compileOutput.size() == 0)
				return true;

			//
			parseOutput(file);
			
			try {
				resource.refreshLocal(IResource.DEPTH_ONE, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	private void parseOutput(IFile file)
	{
		for (String line : m_compileOutput)
		{
			StringTokenizer st = new StringTokenizer(line, ":");
			
			if (st.countTokens() < 3)
				continue;
			
			String srcName = st.nextToken();
			String srcLine = st.nextToken();
			String errMsg  = st.nextToken();

			if (srcName.contains(file.getName()))
			{
				addMarker(file, errMsg, (new Integer(srcLine)).intValue(), IMarker.SEVERITY_ERROR);
			}
		}
	}
	
	private void addMarker(IFile file, String message, int lineNumber, int severity) 
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
	
	private void deleteMarkers(IFile file) 
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
