package org.eclipse.libra.framework.core;

import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;

public enum IOSGIExecutionEnvironment {
	Default, JAVASE6_SERVER;

	@Override
	public String toString() {
		switch (this) {
		case Default:
			return "Default";
		case JAVASE6_SERVER:
			return "JavaSE-1.6-Server";
		}
		return this.name();
	}
	
	public static String[] getExecutionEnvironmentIds() {

		IExecutionEnvironment environment[] = JavaRuntime
				.getExecutionEnvironmentsManager().getExecutionEnvironments();
		IOSGIExecutionEnvironment[] all = IOSGIExecutionEnvironment.values();
		String[] envList = new String[environment.length + all.length];
		int i = 0;
		for (IOSGIExecutionEnvironment e : all) {
			envList[i++] = e.toString();
		}
		for (IExecutionEnvironment e : environment) {
			envList[i++] = e.getId();
		}
		return envList;
	}

}
