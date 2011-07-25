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
package org.eclipse.libra.facet.ui.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.libra.facet.ui.wizards.messages"; //$NON-NLS-1$
	public static String ConvertProjectsToBundlesWizard_Title;
	public static String ConvertProjectsToBundlesWizardPage_AddReferences;
	public static String ConvertProjectsToBundlesWizardPage_AvailableProjects;
	public static String ConvertProjectsToBundlesWizardPage_Description;
	public static String ConvertProjectsToBundlesWizardPage_DeselectAll;
	public static String ConvertProjectsToBundlesWizardPage_ReferencedProjectsNotSelected;
	public static String ConvertProjectsToBundlesWizardPage_SelectAll;
	public static String ConvertProjectsToBundlesWizardPage_SelectionCounter;
	public static String ConvertProjectsToBundlesWizardPage_Title;
	public static String OSGiBundleFacetInstallPage_Description;
	public static String OSGiBundleFacetInstallPage_Name;
	public static String OSGiBundleFacetInstallPage_SymbolicName;
	public static String OSGiBundleFacetInstallPage_Title;
	public static String OSGiBundleFacetInstallPage_Vendor;
	public static String OSGiBundleFacetInstallPage_Version;
	public static String OSGiBundleFacetUninstallPage_Description;
	public static String OSGiBundleFacetUninstallPage_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
