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
package org.eclipse.libra.framework.editor.core.model;

import java.util.Map;
import java.util.Set;

/**
 * @author Kaloyan Raev
 */
public interface IBundle {

	public String getId();

	public String getSymbolicName();

	public String getVersion();

	public String getState();

	public String getLocation();
	
	public Map<String, String> getHeaders();

	public Set<IPackageExport> getPackageExports();

	public Set<IPackageImport> getPackageImports();
	
	public Set<IServiceReference> getRegisteredServices();

	public Set<IServiceReference> getServicesInUse();

}
