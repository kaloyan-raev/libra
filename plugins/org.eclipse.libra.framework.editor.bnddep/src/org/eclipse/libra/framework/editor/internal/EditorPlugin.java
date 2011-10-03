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
package org.eclipse.libra.framework.editor.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Kaloyan Raev
 */
public class EditorPlugin extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID = "org.eclipse.libra.framework.editor";

	private static EditorPlugin plugin = null;

	public EditorPlugin() {
		super();
		plugin = this;
	}

	public static EditorPlugin getDefault() {
		if (plugin == null) {
			plugin = new EditorPlugin();
		}
		return plugin;
	}

	public static IStatus newErrorStatus(Throwable t) {
		return new Status(IStatus.ERROR, PLUGIN_ID, t.getMessage(), t);
	}
	
	/**
	 * Logs the specified status with this plug-in's log.
	 * 
	 * @param status status to log
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}
	
	/**
	 * Logs the specified throwable with this plug-in's log.
	 * 
	 * @param t throwable to log
	 */
	public static void log(Throwable t) {
		if (t instanceof CoreException) {
			log(((CoreException) t).getStatus());
		} else {
			log(newErrorStatus(t));
		}
	}

}
