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

import org.eclipse.libra.framework.editor.core.model.IBundle;

/**
 * @author Christian Dupuis
 * @author Kaloyan Raev
 */
public abstract class BundleDependency {

	private final IBundle exportingBundle;

	private final IBundle importingBundle;

	public BundleDependency(IBundle exportingBundle, IBundle importingBundle) {
		this.exportingBundle = exportingBundle;
		this.importingBundle = importingBundle;
	}

	public IBundle getExportingBundle() {
		return exportingBundle;
	}

	public IBundle getImportingBundle() {
		return importingBundle;
	}

	@Override
	public int hashCode() {
		int hashCode = 17;
		hashCode = 31 * hashCode + exportingBundle.hashCode();
		hashCode = 31 * hashCode + importingBundle.hashCode();
		return hashCode;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BundleDependency)) {
			return false;
		}
		BundleDependency that = (BundleDependency) other;
		if (this.exportingBundle != that.exportingBundle) {
			return false;
		}
		if (this.exportingBundle != null && !this.exportingBundle.equals(that.exportingBundle)) {
			return false;
		}
		if (this.importingBundle != that.importingBundle) {
			return false;
		}
		if (this.importingBundle != null && !this.importingBundle.equals(that.importingBundle)) {
			return false;
		}
		return true;
	}

}
