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
import org.eclipse.libra.framework.editor.core.IOSGiFrameworkAdmin;
import org.eclipse.libra.framework.editor.core.IOSGiFrameworkConsole;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Kaloyan Raev
 */
public class OSGiFrameworkAdminAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof IServer) {
			IServer server = (IServer) adaptableObject;
			if (isLibraLauncher(server)) {
				if (adapterType == IOSGiFrameworkAdmin.class) {
					return new OSGiJMXFrameworkAdmin();
				} else if (adapterType == IOSGiFrameworkConsole.class) {
					return new SimpleOSGiFrameworkConsole();
				}
			}
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { IOSGiFrameworkAdmin.class, IOSGiFrameworkConsole.class };
	}
	
	private boolean isLibraLauncher(IServer server) {
		return null != server.loadAdapter(OSGIFrameworkInstanceBehaviorDelegate.class, null);
	}

}
