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

/**
 * Constants for the PDA debugger.
 */
public interface RhogenConstants 
{	
	public static final String breakpointMarkerId = "com.rhomobile.rhostudio.lineBreakpoint_marker";
	
	/**
	 * Unique identifier for the PDA debug model (value 
	 * <code>org.eclipse.debug.examples.pda</code>).
	 */
	public static final String debugModelId = "com.rhomobile.rhostudio.rhogenDebugModel";	
	
	public static final String debugPerspectiveId = "org.eclipse.debug.ui.DebugPerspective";
}
