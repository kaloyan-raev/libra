/*******************************************************************************
 * Copyright (c) 2009 SpringSource, a divison of VMware, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SpringSource, a division of VMware, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.virgo.ide.runtime.internal.ui.model;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.virgo.ide.management.remote.Bundle;
import org.eclipse.virgo.ide.management.remote.PackageExport;
import org.eclipse.virgo.ide.management.remote.PackageImport;
import org.eclipse.wst.server.core.IServer;
import org.osgi.jmx.framework.BundleStateMBean;


/**
 * Client that connects to the management MBean to retrieve bundle and service
 * data.
 * @author Christian Dupuis
 * @since 2.0.0
 */
public class ManagementConnectorClient {
	
	public static Map<Long, Bundle> getBundles(IServer server) {
		Map<Long, Bundle> map = new HashMap<Long, Bundle>();
		
		try {
			JMXServiceURL u = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:1234/jmxrmi");
			JMXConnector c = JMXConnectorFactory.connect(u);
			MBeanServerConnection mbsc = c.getMBeanServerConnection();
			ObjectName mbeanBean = new ObjectName("osgi.core:type=bundleState,version=1.5");
			BundleStateMBean mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanBean, BundleStateMBean.class);
			TabularData data = mbeanProxy.listBundles();
			Set keys = data.keySet();
			for (Object key : keys) {
				CompositeData bundleInfo = data.get(((Collection) key).toArray());
				String id = bundleInfo.get(BundleStateMBean.IDENTIFIER).toString();
				String symbolicName = bundleInfo.get(BundleStateMBean.SYMBOLIC_NAME).toString();
				String version = bundleInfo.get(BundleStateMBean.VERSION).toString();
				String state = bundleInfo.get(BundleStateMBean.STATE).toString();
				String location = bundleInfo.get(BundleStateMBean.LOCATION).toString();
				Bundle bundle = new Bundle(id, symbolicName, version, state, location);
				
				String[] exportedPackages = (String[]) bundleInfo.get(BundleStateMBean.EXPORTED_PACKAGES);
				for (String epStr : exportedPackages) {
					int column = epStr.indexOf(';');
					String packageName = epStr.substring(0, column);
					String packageVersion = epStr.substring(column + 1, epStr.length());
					bundle.addPackageExport(new PackageExport(packageName, packageVersion));
				}
				
				String[] importedPackages = (String[]) bundleInfo.get(BundleStateMBean.IMPORTED_PACKAGES);
				for (String ipStr : importedPackages) {
					int column = ipStr.indexOf(';');
					String packageName = ipStr.substring(0, column);
					String packageVersion = ipStr.substring(column + 1, ipStr.length());
					bundle.addPackageImport(new PackageImport(packageName, packageVersion, "0"));
				}
				
				map.put(Long.parseLong(id), bundle);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		IServerBehaviour behaviour = (IServerBehaviour) server.loadAdapter(IServerBehaviour.class,
//				new NullProgressMonitor());
//		if (behaviour != null) {
//			try {
//				return behaviour.getVersionHandler().getServerBundleAdminCommand(behaviour).execute();
//			}
//			catch (IOException e) {
//			}
//			catch (TimeoutException e) {
//			}
//		}
		return map;
	}

	public static String execute(IServer server, String cmdLine) {

//		IServerBehaviour behaviour = (IServerBehaviour) server.loadAdapter(IServerBehaviour.class,
//				new NullProgressMonitor());
//		if (behaviour != null) {
//			try {
//				return behaviour.getVersionHandler().getServerBundleAdminExecuteCommand(behaviour, cmdLine).execute();
//			}
//			catch (IOException e) {
//			}
//			catch (TimeoutException e) {
//			}
//		}
		return "<error>";
	}

}
