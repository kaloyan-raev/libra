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
package org.eclipse.virgo.ide.runtime.internal.ui.overview;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.libra.framework.editor.internal.EditorPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.virgo.ide.management.remote.Bundle;
import org.eclipse.virgo.ide.runtime.internal.ui.AbstractBundleEditorPage;
import org.eclipse.virgo.ide.runtime.internal.ui.model.IOSGiFrameworkAdmin;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorPartInput;
import org.eclipse.wst.server.ui.internal.editor.ServerResourceCommandManager;


/**
 * @author Christian Dupuis
 * @author Steffen Pingel
 * @author Kaloyan Raev
 */
@SuppressWarnings("restriction")
public class BundleInformationEditorPage extends AbstractBundleEditorPage {

	private BundleInformationMasterDetailsBlock masterDetailsBlock;

	private ServerResourceCommandManager commandManager;

	protected void createBundleContent(Composite parent) {
		if (mform == null) {
			mform = new ManagedForm(parent);
		}

		FormToolkit toolkit = getFormToolkit(parent.getDisplay());

		sform = mform.getForm();
		sform.getForm().setSeparatorVisible(true);
		sform.getForm().setText("Bundle Information");
		sform.setExpandHorizontal(true);
		sform.setExpandVertical(true);
		sform.setImage(ServerUICore.getLabelProvider().getImage(getServer()));
		toolkit.decorateFormHeading(sform.getForm());

		masterDetailsBlock = new BundleInformationMasterDetailsBlock(this, commandManager.getServerEditor(),
				getServer().getOriginal());
		masterDetailsBlock.createContent(mform);

	}

	@Override
	protected void enablePage() {
		super.enablePage();
		masterDetailsBlock.refresh();
	}
	
	protected void disablePage() {
		super.disablePage();
		masterDetailsBlock.clear();
	}

	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		commandManager = ((ServerEditorPartInput) input).getServerCommandManager();
	}

	public void showOverviewForBundle(final Bundle bundle) {
		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				monitor.beginTask("Updating bundle status from server", 1);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						IOSGiFrameworkAdmin admin = (IOSGiFrameworkAdmin) masterDetailsBlock.getServer()
								.loadAdapter(IOSGiFrameworkAdmin.class, null);
						try {
							masterDetailsBlock.refresh(admin.getBundles());
							masterDetailsBlock.setSelectedBundle(bundle);
						} catch (CoreException e) {
							EditorPlugin.log(e);
						}
					}
				});
				monitor.worked(1);
			}
		};

		try {
			IRunnableContext context = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			context.run(true, true, runnable);
		}
		catch (InvocationTargetException e1) {
		}
		catch (InterruptedException e2) {
		}
	}

}
