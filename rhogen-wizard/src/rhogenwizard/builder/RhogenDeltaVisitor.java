package rhogenwizard.builder;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

class RhogenDeltaVisitor implements IResourceDeltaVisitor 
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException 
	{
		IResource resource = delta.getResource();
		
		switch (delta.getKind()) 
		{
		case IResourceDelta.ADDED:
			// handle added resource
			break;
		case IResourceDelta.REMOVED:
			// handle removed resource
			break;
		case IResourceDelta.CHANGED:
			// handle changed resource
			break;
		}
		
		//return true to continue visiting children.
		return true;
	}
}