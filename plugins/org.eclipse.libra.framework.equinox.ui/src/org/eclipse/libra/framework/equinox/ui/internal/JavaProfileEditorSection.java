/*******************************************************************************
 *    Copyright (c) 2011 Eteration A.S. and others.
 *    All rights reserved. This program and the accompanying materials
 *    are made available under the terms of the Eclipse Public License v1.0
 *    which accompanies this distribution, and is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 *    
 *     Contributors:
 *       Naci Dai 
 *******************************************************************************/

package org.eclipse.libra.framework.equinox.ui.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.libra.framework.core.IOSGIExecutionEnvironment;
import org.eclipse.libra.framework.core.IOSGIFrameworkInstance;
import org.eclipse.libra.framework.equinox.IEquinoxFrameworkInstance;
import org.eclipse.libra.framework.ui.ContextIds;
import org.eclipse.libra.framework.ui.Messages;
import org.eclipse.libra.framework.ui.Trace;
import org.eclipse.pde.internal.core.target.TargetPlatformService;
import org.eclipse.pde.internal.core.target.provisional.ITargetDefinition;
import org.eclipse.pde.internal.ui.SWTFactory;
import org.eclipse.pde.internal.ui.wizards.target.EditTargetDefinitionWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;

@SuppressWarnings("restriction")
public class JavaProfileEditorSection extends ServerEditorSection {
	protected Section section;
	protected IEquinoxFrameworkInstance frameworkInstance;

	protected PropertyChangeListener listener;
	private Combo javaProfileCombo;

	// Avoid hardcoding this at some point
	// private final static String METADATADIR = ".metadata";

	protected boolean updating = false;

	public JavaProfileEditorSection() {
		super();
	}

	/**
	 * Add listeners to detect undo changes and publishing of the server.
	 */
	protected void addChangeListeners() {
		listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (updating)
					return;
				updating = true;
				if (IOSGIFrameworkInstance.PROPERTY_JAVA_PROFILE
						.equals(event.getPropertyName())) {
					validate();
				}
				updating = false;
			}
		};
		server.addPropertyChangeListener(listener);

	}

	/**
	 * Creates the SWT controls for this workbench part.
	 * 
	 * @param parent
	 *            the parent control
	 */
	public void createSection(Composite parent) {
		super.createSection(parent);
		FormToolkit toolkit = getFormToolkit(parent.getDisplay());
		section = toolkit.createSection(parent, ExpandableComposite.TWISTIE
				| ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR
				| Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);
		section.setText(Messages.javaProfileSection);
		section.setDescription(Messages.javaProfileSectionDescription);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_FILL));

		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 15;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_FILL));
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(composite, ContextIds.FRAMEWORK_INSTANCE_EDITOR);
		whs.setHelp(section, ContextIds.FRAMEWORK_INSTANCE_EDITOR);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);

		// Java Profiles Selection
		Label label = createLabel(toolkit, composite,
				Messages.javaProfileSection);
		GridData data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		label.setLayoutData(data);
		String[] envList =IOSGIExecutionEnvironment.getExecutionEnvironmentIds();
		javaProfileCombo = SWTFactory.createCombo(composite, SWT.SINGLE
				| SWT.BORDER | SWT.READ_ONLY, 1, envList);
		javaProfileCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				frameworkInstance.setJavaProfile(javaProfileCombo.getText());
			}
		});

		initialize();
	}

	protected Label createLabel(FormToolkit toolkit, Composite parent,
			String text) {
		Label label = toolkit.createLabel(parent, text);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		return label;
	}

	/**
	 * @see ServerEditorSection#dispose()
	 */
	public void dispose() {

	}

	/**
	 * @see ServerEditorSection#init(IEditorSite, IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);

		// Cache workspace and default deploy paths
		if (server != null) {
			frameworkInstance = (IEquinoxFrameworkInstance) server.loadAdapter(
					IEquinoxFrameworkInstance.class, null);
			addChangeListeners();
		}
		initialize();
	}

	/**
	 * Initialize the fields in this editor.
	 */
	protected void initialize() {
		if (frameworkInstance == null)
			return;
		updating = true;

		IRuntime runtime = server.getRuntime();
		String id = frameworkInstance.getJavaPofile();

		if (javaProfileCombo != null) {
			String[] envList =IOSGIExecutionEnvironment.getExecutionEnvironmentIds();
			int i = 0;
			for (String e : envList) {
				if (id.equals(e)) {
					javaProfileCombo.select(i);
					break;
				}
				i++;
			}
		}
		updating = false;
		validate();
	}

	protected void validate() {
		if (frameworkInstance != null) {
			// Validate
		}
		// All is okay, clear any previous error
		setErrorMessage(null);
	}

	protected void handleEdit() {

		try {
			if (frameworkInstance != null
					&& frameworkInstance.getFrameworkInstanceConfiguration() != null) {

				ITargetDefinition original = frameworkInstance
						.getFrameworkInstanceConfiguration()
						.getTargetDefinition();

				EditTargetDefinitionWizard wizard = new EditTargetDefinitionWizard(
						original, true);
				wizard.setWindowTitle(Messages.configurationEditorTargetDefinitionTitle);
				WizardDialog dialog = new WizardDialog(this.getShell(), wizard);
				if (dialog.open() == Window.OK) {
					// Replace all references to the original with the new
					// target
					ITargetDefinition newTarget = wizard.getTargetDefinition();
					frameworkInstance.getFrameworkInstanceConfiguration()
							.setTargetDefinition(newTarget);
					TargetPlatformService.getDefault().saveTargetDefinition(
							newTarget);
				}
			}
		} catch (CoreException e) {
			Trace.trace(Trace.SEVERE,
					"failed to update target platform definition");
		}
	}
}