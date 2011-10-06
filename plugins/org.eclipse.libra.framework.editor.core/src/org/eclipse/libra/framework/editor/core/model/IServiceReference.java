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
public interface IServiceReference {
	
	public enum Type {
		IN_USE, REGISTERED
	}

	public Type getType();

	public Long getBundleId();

	public String[] getClazzes();
	
	public Set<Long> getUsingBundleIds();

	public Map<String, String> getProperties();

}
