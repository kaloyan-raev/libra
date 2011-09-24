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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.openmbean.TabularData;

import org.osgi.jmx.framework.PackageStateMBean;

/**
 * @author Kaloyan Raev
 */
public class PackagesData {
	
	private Map<PackageKey, Long> map = new HashMap<PackagesData.PackageKey, Long>();
	
	public PackagesData(PackageStateMBean mbean) throws IOException {
		TabularData data = mbean.listPackages();
		
		Set<List> keys = (Set<List>) data.keySet();
		for (List key : keys) {
			String name = (String) key.get(0);
			String version = (String) key.get(1);
			Long exportingBundleId = ((Long[]) key.get(2))[0];
			map.put(new PackageKey(name, version), exportingBundleId);
		}
	}
	
	public Long getExportingBundleId(String packageName, String packageVersion) {
		return map.get(new PackageKey(packageName, packageVersion));
	}
	
	class PackageKey {
		
		private String name;
		private String version;
		
		PackageKey(String name, String version) {
			this.name = name;
			this.version = version;
		}

		public String getName() {
			return name;
		}
		
		public String getVersion() {
			return version;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result
					+ ((version == null) ? 0 : version.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PackageKey other = (PackageKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (version == null) {
				if (other.version != null)
					return false;
			} else if (!version.equals(other.version))
				return false;
			return true;
		}

		private PackagesData getOuterType() {
			return PackagesData.this;
		}
		
	}

}
