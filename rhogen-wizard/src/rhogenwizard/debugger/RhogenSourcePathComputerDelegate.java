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
package rhogenwizard.debugger;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.FolderSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.ProjectSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.WorkspaceSourceContainer;

import rhogenwizard.constants.ConfigurationConstants;

/**
 * Computes the default source lookup path for a PDA launch configuration.
 * The default source lookup path is the folder or project containing 
 * the PDA program being launched. If the program is not specified, the workspace
 * is searched by default.
 */
public class RhogenSourcePathComputerDelegate implements ISourcePathComputerDelegate 
{	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.core.sourcelookup.ISourcePathComputerDelegate#computeSourceContainers(org.eclipse.debug.core.ILaunchConfiguration, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException 
	{
		String m_projectName   = configuration.getAttribute(ConfigurationConstants.projectNameCfgAttribute, "");
		ISourceContainer sourceContainer = null;
		
		if (m_projectName != null)
		{
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember("/" + m_projectName + "/build.yml");
		
			if (resource != null) 
			{
				IContainer container = resource.getParent();
			
				if (container.getType() == IResource.PROJECT) 
				{
					sourceContainer = new ProjectSourceContainer((IProject)container, false);
				} 
				else if (container.getType() == IResource.FOLDER) 
				{
					sourceContainer = new FolderSourceContainer(container, false);
				}
			}
		}
		
		if (sourceContainer == null) 
		{
			sourceContainer = new WorkspaceSourceContainer();
		}
		
		return new ISourceContainer[]{sourceContainer};
	}
}
