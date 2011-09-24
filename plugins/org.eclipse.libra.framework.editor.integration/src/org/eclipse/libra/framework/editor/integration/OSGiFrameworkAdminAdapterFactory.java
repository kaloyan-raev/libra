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
package org.eclipse.libra.framework.editor.integration;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.libra.framework.core.OSGIFrameworkInstanceBehaviorDelegate;
import org.eclipse.virgo.ide.runtime.internal.ui.model.IOSGiFrameworkAdmin;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Kaloyan Raev
 */
public class OSGiFrameworkAdminAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof IServer && adapterType == IOSGiFrameworkAdmin.class) {
			IServer server = (IServer) adaptableObject;
			OSGIFrameworkInstanceBehaviorDelegate behavior = (OSGIFrameworkInstanceBehaviorDelegate) server
					.loadAdapter(OSGIFrameworkInstanceBehaviorDelegate.class, null);
			if (behavior != null) {
				return new OSGiJMXFrameworkAdmin();
			}
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { IOSGiFrameworkAdmin.class };
	}

}
