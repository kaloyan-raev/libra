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
package org.eclipse.libra.facet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.libra.facet.internal.LibraFacetPlugin;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

public class OSGiBundleFacetUtils {
	
	public static final String OSGI_BUNDLE = "osgi.bundle"; //$NON-NLS-1$
	public static final IProjectFacet OSGI_BUNDLE_FACET = ProjectFacetsManager.getProjectFacet(OSGI_BUNDLE);
	public static final IProjectFacetVersion OSGI_BUNDLE_FACET_42 = OSGI_BUNDLE_FACET.getVersion("4.2"); //$NON-NLS-1$
	
	public static final String WEB_FACET = "jst.web"; //$NON-NLS-1$
	public static final String JPA_FACET = "jpt.jpa"; //$NON-NLS-1$
	
	public static final String FEATURE_NATURE_ID = "org.eclipse.pde.FeatureNature"; //$NON-NLS-1$
	public static final String SITE_NATURE_ID = "org.eclipse.pde.UpdateSiteNature"; //$NON-NLS-1$
	public static final String MANIFEST_BUILDER_ID = "org.eclipse.pde.ManifestBuilder"; //$NON-NLS-1$
	public static final String SCHEMA_BUILDER_ID = "org.eclipse.pde.SchemaBuilder"; //$NON-NLS-1$
	
	public static final Path REQUIRED_PLUGINS_CONTAINER_PATH = new Path("org.eclipse.pde.core.requiredPlugins"); //$NON-NLS-1$
	
	public static final String BUILD_PROPERTIES = "build.properties"; //$NON-NLS-1$
	public static final String CONTEXTROOT = "context-root"; //$NON-NLS-1$
	
	public static final String WEB_INF_CLASSES = "WEB-INF/classes/"; //$NON-NLS-1$
	public static final String META_INF = "META-INF"; //$NON-NLS-1$
	public static final String MANIFEST_URI = "META-INF/MANIFEST.MF"; //$NON-NLS-1$
	public static final IPath MANIFEST_PATH = new Path(MANIFEST_URI);
	public static final String VIRTUAL_COMPONENT_URI = ".settings/org.eclipse.wst.common.component"; //$NON-NLS-1$
	public static final IPath VIRTUAL_COMPONENT_PATH = new Path(VIRTUAL_COMPONENT_URI);
	
	public static final String WEB_CONTEXT_PATH_HEADER = "Web-ContextPath"; //$NON-NLS-1$
	public static final String META_PERSISTENCE_HEADER = "Meta-Persistence"; //$NON-NLS-1$
	
	public static final String JAVAX_SERVLET_PACKAGE = "javax.servlet"; //$NON-NLS-1$
	public static final String JAVAX_SERVLET_HTTP_PACKAGE = "javax.servlet.http"; //$NON-NLS-1$
	public static final String JAVAX_SERVLET_JSP_PACKAGE = "javax.servlet.jsp"; //$NON-NLS-1$
	public static final String JAVAX_SERVLET_JSP_EL_PACKAGE = "javax.servlet.jsp.el"; //$NON-NLS-1$
	public static final String JAVAX_SERVLET_JSP_TAGEXT_PACKAGE = "javax.servlet.jsp.tagext"; //$NON-NLS-1$
	public static final String JAVAX_EL_PACKAGE = "javax.el"; //$NON-NLS-1$
	public static final String JAVAX_PERSISTENCE_PACKAGE = "javax.persistence;jpa=\"%s\""; //$NON-NLS-1$
	
	public static boolean hasPluginNature(IProject project) throws CoreException {
		return project.hasNature(IBundleProjectDescription.PLUGIN_NATURE);
	}
	
	public static boolean hasFeatureNature(IProject project) throws CoreException {
		return project.hasNature(FEATURE_NATURE_ID);
	}
	
	public static boolean hasUpdateSiteNature(IProject project) throws CoreException {
		return project.hasNature(SITE_NATURE_ID);
	}
	
	public static boolean isOSGiBundle(IProject project) throws CoreException {
		return FacetedProjectFramework.hasProjectFacet(project, OSGI_BUNDLE);
	}

	public static boolean isJavaProject(IProject project) throws CoreException {
		return project.hasNature(JavaCore.NATURE_ID);
	}

	public static boolean isWebProject(IProject project) throws CoreException {
		return FacetedProjectFramework.hasProjectFacet(project, WEB_FACET);
	}
	
	public static boolean isWebApplicationBundle(IProject project) throws CoreException {
		return isWebProject(project) && isOSGiBundle(project);
	}	

	public static boolean isJpaProject(IProject project) throws CoreException {
		return FacetedProjectFramework.hasProjectFacet(project, JPA_FACET);
	}
	
	public static boolean isRequiredPlugins(IClasspathEntry entry) {
		return REQUIRED_PLUGINS_CONTAINER_PATH.equals(entry.getPath());
	}
	
	public static boolean hasRequiredPlugins(IClasspathEntry[] entries) {
		for (IClasspathEntry entry : entries) {
			if (isRequiredPlugins(entry)) {
				return true;
			}
		}
		return false;
	}
	
	public static IBundleProjectDescription getBundleProjectDescription(IProject project) throws CoreException {
    	IBundleProjectService bundleProjectService = LibraFacetPlugin.getDefault().getBundleProjectService();
    	return bundleProjectService.getDescription(project);
	}

	public static String getContextRootFromWTPModel(IProject project) {
		IVirtualComponent component = ComponentCore.createComponent(project);
		String contextRoot = component.getMetaProperties().getProperty(CONTEXTROOT);
		
		// return null if context root is empty
		if (contextRoot == null || contextRoot.length() == 0) 
			return null;
		
		// add leading slash if not available
		if (contextRoot.charAt(0) != '/') {
			contextRoot = '/' + contextRoot;
		}
		return contextRoot;
	}
	
	public static void setContextRootInWTPModel(IProject project, String contextRoot) {
		IVirtualComponent component = ComponentCore.createComponent(project);
		component.setMetaProperty(CONTEXTROOT, contextRoot);
	}	
	
	public static String getContextRootFromPDEModel(IProject project) throws CoreException {
		IBundleProjectDescription bundleProjectDescription = getBundleProjectDescription(project);
		String rootContext = bundleProjectDescription.getHeader(WEB_CONTEXT_PATH_HEADER);
		return rootContext;
	}
	
	public static void setContextRootInPDEModel(IProject project, String contextRoot, IProgressMonitor monitor) throws CoreException {
		IBundleProjectDescription bundleProjectDescription = getBundleProjectDescription(project); 
		bundleProjectDescription.setHeader(WEB_CONTEXT_PATH_HEADER, contextRoot);
		bundleProjectDescription.apply(monitor);
	}	
	
}
