package rhogenwizard.builder;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;

class RhogenResourceVisitor implements IResourceVisitor 
{
	public boolean visit(IResource resource) 
	{
		//return true to continue visiting children.
		return true;
	}
}
