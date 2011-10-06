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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.libra.framework.editor.core.model.IBundle;

/**
 * @author Christian Dupuis
 * @author Kaloyan Raev
 */
public class BundleDependencyContentResult {

	private final Set<IBundle> bundles;

	private final Map<Integer, Set<IBundle>> incomingDependencies = new HashMap<Integer, Set<IBundle>>();

	private final Map<Integer, Set<IBundle>> outgoingDependencies = new HashMap<Integer, Set<IBundle>>();

	public BundleDependencyContentResult(Set<IBundle> bundles) {
		this.bundles = bundles;
	}

	public void addIncomingDependency(Integer level, IBundle bundleDependency) {
		if (!incomingDependencies.containsKey(level)) {
			incomingDependencies.put(level, new TreeSet<IBundle>(new Comparator<IBundle>() {

				public int compare(IBundle o1, IBundle o2) {
					return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
				}
			}));
		}
		incomingDependencies.get(level).add(bundleDependency);
	}

	public void addOutgoingDependency(Integer level, IBundle bundleDependency) {
		if (!outgoingDependencies.containsKey(level)) {
			outgoingDependencies.put(level, new TreeSet<IBundle>(new Comparator<IBundle>() {

				public int compare(IBundle o1, IBundle o2) {
					return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
				}
			}));
		}
		outgoingDependencies.get(level).add(bundleDependency);
	}

	public Set<IBundle> getBundles() {
		return bundles;
	}

	public Map<Integer, Set<IBundle>> getIncomingDependencies() {
		return incomingDependencies;
	}

	public Map<Integer, Set<IBundle>> getOutgoingDependencies() {
		return outgoingDependencies;
	}

	public Integer getIncomingDegree() {
		int degree = 0;
		for (Integer integer : incomingDependencies.keySet()) {
			degree = Math.max(degree, integer);
		}
		return degree;
	}

	public Integer getOutgoingDegree() {
		int degree = 0;
		for (Integer integer : outgoingDependencies.keySet()) {
			degree = Math.max(degree, integer);
		}
		return degree;
	}
}
