/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Bjorn Freeman-Benson - initial API and implementation
 *******************************************************************************/
package rhogenwizard.debugger.model;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;

import rhogenwizard.debugger.RhogenConstants;

/**
 * PDA line breakpoint
 */
public class RhogenLineBreakpoint extends LineBreakpoint 
{	
	public RhogenLineBreakpoint() 
	{
	}
	
	public RhogenLineBreakpoint(final IResource resource, final int lineNumber) throws CoreException 
	{
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() 
		{
			public void run(IProgressMonitor monitor) throws CoreException 
			{
				IMarker marker = resource.createMarker(RhogenConstants.breakpointMarkerId);
				
				setMarker(marker);
				
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(IMarker.MESSAGE, "Test Line Breakpoint: "
						+ resource.getName() + " [line: " + lineNumber + "]");
			}
		};
		
		run(getMarkerRule(resource), runnable);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IBreakpoint#getModelIdentifier()
	 */
	public String getModelIdentifier()
	{
		return RhogenConstants.debugModelId;
	}
	
	public String getResourcePath()
	{
		IPath resPath = getMarker().getResource().getFullPath();
		
		String path = resPath.toOSString();
		path = path.replace('\\', '/');
		String[] segments = path.split("app/");
		
		if (segments.length > 1)
			return segments[1];
		
		return segments[0];
	}
}
