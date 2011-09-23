package org.eclipse.virgo.ide.runtime.internal.ui.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.osgi.jmx.framework.ServiceStateMBean;

public class ServicesData {
	
	private Map<Long, ServiceInfo> map = new HashMap<Long, ServicesData.ServiceInfo>();

	public ServicesData(ServiceStateMBean serviceStateMBean) throws IOException {
		TabularData data = serviceStateMBean.listServices();
		
		Set<List> keys = (Set<List>) data.keySet();
		for (List key : keys) {
			CompositeData serviceInfo = data.get(((Collection) key).toArray());
			Long serviceId = (Long) serviceInfo.get(ServiceStateMBean.IDENTIFIER);
			Long bundleId = (Long) serviceInfo.get(ServiceStateMBean.BUNDLE_IDENTIFIER);
			String[] objectClass = (String[]) serviceInfo.get(ServiceStateMBean.OBJECT_CLASS);
			Long[] usingBundles = (Long[]) serviceInfo.get(ServiceStateMBean.USING_BUNDLES);
			map.put(serviceId, new ServiceInfo(serviceId, bundleId, objectClass, usingBundles));
		}
	}
	
	public ServiceInfo getService(Long serviceId) {
		return map.get(serviceId);
	}
	
	class ServiceInfo {
		
		private Long serviceId;
		private Long bundleId;
		private String[] objectClass;
		private Long[] usingBundles;
		
		ServiceInfo(Long serviceId, Long bundleId, String[] objectClass, Long[] usingBundles) {
			this.serviceId = serviceId;
			this.bundleId = bundleId;
			this.objectClass = objectClass;
			this.usingBundles = usingBundles;
		}

		public Long getServiceId() {
			return serviceId;
		}

		public Long getBundleId() {
			return bundleId;
		}

		public String[] getObjectClass() {
			return objectClass;
		}

		public Long[] getUsingBundles() {
			return usingBundles;
		}
		
	}

}
