package rhogenwizard.builder.rhodes;

import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

import rhogenwizard.constants.CommonConstants;

class ResourceVisitor extends MarkerHelper implements IResourceVisitor 
{
	List<String> m_compileOutput = null;
	
	public ResourceVisitor(List<String> compileOutput)
	{
		m_compileOutput = compileOutput;
	}
	
	public boolean visit(IResource resource) 
	{	
		if (resource instanceof IFile && resource.getName().endsWith(CommonConstants.rubyFileExt)) 
		{
			IFile file = (IFile) resource;
			deleteMarkers(file);		

			try 
			{
				resource.refreshLocal(IResource.DEPTH_ONE, null);
			
				if (m_compileOutput == null || m_compileOutput.size() == 0)
					return true;
				
				parseOutput(file);
				
				resource.refreshLocal(IResource.DEPTH_ONE, null);
			} 
			catch (CoreException e) 
			{
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
}
