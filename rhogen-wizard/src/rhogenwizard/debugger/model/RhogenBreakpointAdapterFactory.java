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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.dltk.ruby.internal.ui.editor.RubyEditor;

/**
 * Creates a toggle breakpoint adapter
 */
public class RhogenBreakpointAdapterFactory implements IAdapterFactory 
{
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType) 
	{
		if (adaptableObject instanceof RubyEditor) 
		{
			RubyEditor editorPart = (RubyEditor) adaptableObject;
			
			IResource resource = (IResource) editorPart.getEditorInput().getAdapter(IResource.class);
		
			if (resource != null) 
			{
				String extension = resource.getFileExtension();
				
				if (extension != null && extension.equals("rb")) 
				{
					return new RhogenLineBreakpointAdapter();
				}
			}			
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() 
	{
		return new Class[] { IToggleBreakpointsTarget.class };
	}
}
