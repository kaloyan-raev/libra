/*******************************************************************************
 * Copyright (c) 2011 SAP AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SAP AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.virgo.ide.runtime.internal.ui.model;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Kaloyan Raev
 */
public interface IOSGiFrameworkConsole {
	
	public String executeCommand(String command) throws CoreException;

}
