package org.eclipse.libra.framework.ui.internal.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServer.IOperationListener;
import org.eclipse.wst.server.ui.IServerModule;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;

public class SyncStartAction  implements IObjectActionDelegate {
	protected String launchMode = ILaunchManager.RUN_MODE;
	
	private IServer selectedServer;
	private IModule selectedModule;
	
	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
	}
	
	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		selectedServer = null;
		selectedModule = null;
		if (!selection.isEmpty()) {
			if (selection instanceof IStructuredSelection) {
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				if (obj instanceof IServer) {
					selectedServer = (IServer)obj;
				}
				else if (obj instanceof IServerModule) {
					IServerModule sm = (IServerModule)obj;
					IModule [] module = sm.getModule();
					selectedModule = module[module.length - 1];
					if (selectedModule != null)
						selectedServer = sm.getServer();
				}
			}
		}
	}
	




	public static void start(IServer server, String launchMode, final Shell shell) {
		if (server.getServerState() != IServer.STATE_STARTED) {
			if (!ServerUIPlugin.saveEditors())
				return;
			
			/*final IAdaptable info = new IAdaptable() {
				public Object getAdapter(Class adapter) {
					if (Shell.class.equals(adapter))
						return shell;
					return null;
				}
			};*/
			try {
				server.synchronousStart(launchMode, (IProgressMonitor)null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if (shell != null && !ServerUIPlugin.promptIfDirty(shell, server))
				return;
			
			try {
				String launchMode2 = launchMode;
				if (launchMode2 == null)
					launchMode2 = server.getMode();
				server.restart(launchMode2, (IOperationListener) null);
			} catch (Exception e) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "Error restarting server", e);
				}
			}
		}
	}

	public void run(IAction action) {
		if (selectedServer.getServerState() != IServer.STATE_STARTED) {
			if (!ServerUIPlugin.saveEditors())
				return;
			
			/*final IAdaptable info = new IAdaptable() {
				public Object getAdapter(Class adapter) {
					if (Shell.class.equals(adapter))
						return shell;
					return null;
				}
			};*/
			try {
				selectedServer.synchronousStart(launchMode, (IProgressMonitor)null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
		
			
			try {
				String launchMode2 = launchMode;
				if (launchMode2 == null)
					launchMode2 = selectedServer.getMode();
				selectedServer.restart(launchMode2, (IOperationListener) null);
			} catch (Exception e) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "Error restarting server", e);
				}
			}
		}
	}


}
