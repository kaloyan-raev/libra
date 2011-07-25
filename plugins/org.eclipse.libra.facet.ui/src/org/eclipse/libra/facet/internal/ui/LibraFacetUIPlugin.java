/*******************************************************************************
 * Copyright (c) 2011 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kaloyan Raev (SAP AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.libra.facet.internal.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class LibraFacetUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.libra.facet.ui"; //$NON-NLS-1$

	// The shared instance
	private static LibraFacetUIPlugin plugin;
	
	/**
	 * The constructor
	 */
	public LibraFacetUIPlugin() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static void logError(String msg) {
        logError(msg, null);
    }

	/**
	 * Log the specified exception or error.
	 */
	public static void logError(Throwable throwable) {
		logError(throwable.getLocalizedMessage(), throwable);
	}

	/**
	 * Log the specified message and exception or error.
	 */
	public static void logError(String msg, Throwable throwable) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, throwable));
	}
	
	/**
	 * Log the specified status.
	 */
	public static void log(IStatus status) {
		plugin.getLog().log(status);
    }
		
	/**
	 * Log the specified message and exception or error.
	 */
	public static void logInfo(String msg) {
		log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, msg, null));
	}

}
