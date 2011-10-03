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

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.virgo.ide.management.remote.Bundle;

/**
 * @author Kaloyan Raev
 */
public interface IOSGiFrameworkAdmin {
	
	public Map<Long, Bundle> getBundles() throws CoreException;
	
	public void startBundle(long bundleId) throws CoreException;
	
	public void stopBundle(long bundleId) throws CoreException;
	
	public void refreshBundle(long bundleId) throws CoreException;
	
	public void updateBundle(long bundleId) throws CoreException;
	
	public String executeCommand(String command) throws CoreException;

}
