package org.eclipse.libra.framework.editor.integration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.libra.framework.editor.core.IOSGiFrameworkConsole;
import org.eclipse.libra.framework.editor.integration.internal.IntegrationPlugin;

public class SimpleOSGiFrameworkConsole implements IOSGiFrameworkConsole {

	public String executeCommand(String command) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, IntegrationPlugin.PLUGIN_ID, "Shell commands not supported"));
	}

}
