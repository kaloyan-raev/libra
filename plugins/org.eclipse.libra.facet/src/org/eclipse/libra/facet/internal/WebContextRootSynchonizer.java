/*******************************************************************************
 * Copyright (c) 2011 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Dimov (SAP AG) - initial API and implementation
 *     Kaloyan Raev (SAP AG)
 *******************************************************************************/
package org.eclipse.libra.facet.internal;

import static org.eclipse.libra.facet.OSGiBundleFacetUtils.MANIFEST_PATH;
import static org.eclipse.libra.facet.OSGiBundleFacetUtils.VIRTUAL_COMPONENT_PATH;
import static org.eclipse.libra.facet.OSGiBundleFacetUtils.getContextRootFromPDEModel;
import static org.eclipse.libra.facet.OSGiBundleFacetUtils.getContextRootFromWTPModel;
import static org.eclipse.libra.facet.OSGiBundleFacetUtils.isWebApplicationBundle;
import static org.eclipse.libra.facet.OSGiBundleFacetUtils.setContextRootInPDEModel;
import static org.eclipse.libra.facet.OSGiBundleFacetUtils.setContextRootInWTPModel;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;

/**
 * This class is a resource change listener that watches for changes in the
 * "web context root" in either the WTP and PDE models and synchronizes them.
 * Meaning, if the web context root changes in the PDE model, it is
 * automatically changed in the WTP model, and vice versa.
 * 
 * <p>
 * This resource change listener is registered and unregistered for events in
 * the bundle's activator.
 * </p>
 */
public class WebContextRootSynchonizer implements IResourceChangeListener {

	public void resourceChanged(IResourceChangeEvent event) {
		// we are only interested in POST_CHANGE events
        if (event.getType() != IResourceChangeEvent.POST_CHANGE)
        	return;
        
        final Set<IProject> projectsWithModifiedWTPModel = new HashSet<IProject>();
        final Set<IProject> projectsWithModifiedPDEModel = new HashSet<IProject>();
         
        IResourceDelta rootDelta = event.getDelta();
        IResourceDelta[] projectDeltas = rootDelta.getAffectedChildren();
        for (IResourceDelta projectDelta : projectDeltas) {
        	IProject project = (IProject) projectDelta.getResource();
        	
        	// first check if the modified project is a web application bundle
        	if (!isWAB(project)) 
        		continue;
        	
	        // get the delta, if any, for the org.eclipse.wst.common.component file
	        IResourceDelta componentDelta = projectDelta.findMember(VIRTUAL_COMPONENT_PATH);
	        if (isContentChanged(componentDelta)) {
	        	// add the project to the list of affected projects
	        	projectsWithModifiedWTPModel.add(project);
	        }
	        
	        // get the delta, if any, for the MANIFEST.MF file
	        IResourceDelta manifestDelta = projectDelta.findMember(getManifestPath(project)); 
	        if (isContentChanged(manifestDelta)) {
	        	// add the project to the list of modified projects
	        	projectsWithModifiedPDEModel.add(project);
	        }
        }
        
        if (projectsWithModifiedWTPModel.size() > 0 || projectsWithModifiedPDEModel.size() > 0) {
			// process in a separate job that all projects are identified for possible change in the context root. 
			// reading and writing to models involves expensive I/O operation that should be executed outside the 
        	// resource change listener. 
        	new Job(Messages.WebContextRootSynchonizer_JobName) {
        		@Override
    			protected IStatus run(IProgressMonitor monitor) {
    				// process all projects that are identified for possible change in the context root
    				for (IProject project : projectsWithModifiedWTPModel) {
    					try {
    						// trigger a build to give opportunity to the models to refresh
    						project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
    						// get the new context root from the WTP model
    						String newContextRoot = getContextRootFromWTPModel(project);
    						// get the old context root from the PDE model
    						String oldContextRoot = getContextRootFromPDEModel(project);
    						// check the context roots and decide if updating the one in the PDE model is needed
    						if (!areEqual(newContextRoot, oldContextRoot)) {
    							setContextRootInPDEModel(project, newContextRoot, monitor);
    						}
    					} catch (CoreException e) {
    						LibraFacetPlugin.logError(NLS.bind(Messages.WebContextRootSynchonizer_UpdatingPDEModelFailed, project.getName()), e);	
    					}
    				}
    				
    				for (IProject project : projectsWithModifiedPDEModel) {
    					try {
    						// trigger a build to give opportunity to the models to refresh
    						project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
	    					// get the new context root from the PDE model
    						String newContextRoot = getContextRootFromPDEModel(project);
    						// get the old context root from the WTP model
    						String oldContextRoot = getContextRootFromWTPModel(project);
    						// check the context roots and decide if updating the one in the WTP model is needed
    						if (!areEqual(newContextRoot, oldContextRoot)) {
    							setContextRootInWTPModel(project, newContextRoot);
    						}
    					} catch (CoreException e) {
    						LibraFacetPlugin.logError(NLS.bind(Messages.WebContextRootSynchonizer_UpdatingWTPModelFailed, project.getName()), e);	
    					}
    				}
    				return Status.OK_STATUS;
    			}
        		
        		private boolean areEqual(String value1, String value2) {
        			if (value1 == null) 
        				return value2 == null;
        			return value1.equals(value2);
        		}
        	}.schedule();
        }
	}

	private boolean isWAB(IProject project) {
		boolean result = false;
    	try {
			result = isWebApplicationBundle(project);
		} catch (CoreException e) {
			LibraFacetPlugin.logError(e);
			// do nothing - assume the project is not a web application bundle
		}
    	return result;
	}
	
	private boolean isContentChanged(IResourceDelta delta) {
		return delta != null && delta.getKind() == IResourceDelta.CHANGED
				&& (delta.getFlags() & IResourceDelta.CONTENT) != 0;
	}
	
	private IPath getManifestPath(IProject project) {
		// get the bundle root first
		IPath bundleRoot = null;
		try {
			IBundleProjectService bundleProjectService = LibraFacetPlugin.getDefault().getBundleProjectService();
			IBundleProjectDescription bundleProjectDescription = bundleProjectService.getDescription(project);
			bundleRoot = bundleProjectDescription.getBundleRoot();
		} catch (CoreException e) {
			LibraFacetPlugin.logError(e);
			// do nothing - leave null for bundle root, which is equivalent to project root
		}
		
		if (bundleRoot == null) {
			return MANIFEST_PATH;
		} else {
			return bundleRoot.append(MANIFEST_PATH);
		}
	}

}
