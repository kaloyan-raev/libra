/*******************************************************************************
 * Copyright (c) 2010, 2011 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kaloyan Raev (SAP AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.libra.facet.internal;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The activator class controls the plug-in life cycle
 */
public class LibraFacetPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.libra.facet"; //$NON-NLS-1$

	// The shared instance
	private static LibraFacetPlugin plugin;
	
	private ServiceReference<IBundleProjectService> ref;
	private IBundleProjectService service;
	private WebContextRootSynchonizer webContextRootListener;
	
	/**
	 * The constructor
	 */
	public LibraFacetPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		this.ref = context.getServiceReference(IBundleProjectService.class);
		this.service = (IBundleProjectService) context.getService(ref);
		
		webContextRootListener = new WebContextRootSynchonizer();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(webContextRootListener, IResourceChangeEvent.POST_CHANGE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(webContextRootListener);
		
		context.ungetService(this.ref);
		
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static LibraFacetPlugin getDefault() {
		return plugin;
	}
	
	public IBundleProjectService getBundleProjectService() {
		return service;
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

