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
package org.eclipse.virgo.ide.runtime.internal.ui.model;

import org.eclipse.virgo.ide.management.remote.Bundle;


/**
 * @author Christian Dupuis
 * @author Kaloyan Raev
 */
public abstract class BundleDependency {

	private final Bundle exportingBundle;

	private final Bundle importingBundle;

	public BundleDependency(Bundle exportingBundle, Bundle importingBundle) {
		this.exportingBundle = exportingBundle;
		this.importingBundle = importingBundle;
	}

	public Bundle getExportingBundle() {
		return exportingBundle;
	}

	public Bundle getImportingBundle() {
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
