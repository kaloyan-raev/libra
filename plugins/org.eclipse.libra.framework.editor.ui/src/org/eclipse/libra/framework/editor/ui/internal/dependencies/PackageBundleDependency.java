/*******************************************************************************
 * Copyright (c) 2009, 2011 SpringSource, a divison of VMware, Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SpringSource, a division of VMware, Inc. - initial API and implementation
 *     SAP AG - moving to Eclipse Libra project and enhancements
 *******************************************************************************/
package org.eclipse.libra.framework.editor.ui.internal.dependencies;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.libra.framework.editor.core.model.IBundle;
import org.eclipse.libra.framework.editor.core.model.IPackageImport;

/**
 * @author Christian Dupuis
 * @author Kaloyan Raev
 */
public class PackageBundleDependency extends BundleDependency {

	private final Set<IPackageImport> packageImport = new HashSet<IPackageImport>();

	public PackageBundleDependency(IBundle exportingBundle, IBundle importingBundle) {
		super(exportingBundle, importingBundle);
	}

	public void addPackageImport(IPackageImport pe) {
		packageImport.add(pe);
	}

	@Override
	public int hashCode() {
		int hashCode = 17;
		hashCode = 31 * hashCode + packageImport.hashCode();
		return 31 * hashCode + super.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PackageBundleDependency)) {
			return false;
		}
		PackageBundleDependency that = (PackageBundleDependency) other;
		if (!this.packageImport.equals(that.packageImport)) { // null safe guaranteed by the final modifier
			return false;
		}
		return super.equals(other);
	}

}
