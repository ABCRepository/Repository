/*****************************************************************************
 * Copyright (c) 2014 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  CEA LIST - Initial API and implementation
 *****************************************************************************/
package org.eclipse.papyrus.moka.ui.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.window.Window;
import org.eclipse.papyrus.infra.widgets.editors.TreeSelectorDialog;
import org.eclipse.papyrus.infra.widgets.providers.WorkspaceContentProvider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class MokaProjectSelection extends SelectionAdapter {

	private static final String DIALOG_NAME = "Please select an fUML model";

	protected transient Text projectSelection;

	protected transient IFile currentSelection;

	protected transient MokaRunConfigurationTab launchConfigTab;

	public MokaProjectSelection(Text projectSelection, MokaRunConfigurationTab tab) {
		this.projectSelection = projectSelection;
		this.launchConfigTab = tab;
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		TreeSelectorDialog dialog = new TreeSelectorDialog(Display.getCurrent().getActiveShell());
		dialog.setTitle(DIALOG_NAME);
		dialog.setContentProvider(new WorkspaceContentProvider());
		dialog.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());

		if (this.currentSelection != null && this.currentSelection.exists()) {
			dialog.setInitialSelections(new IFile[] { this.currentSelection });
		}

		dialog.open();
		Object[] selection = dialog.getResult();
		if (dialog.getReturnCode() == Window.OK && selection.length > 0 && (selection[0] instanceof IFile)) {
			this.currentSelection = (IFile) selection[0];
			URI fileURI = URI.createPlatformResourceURI(this.currentSelection.getFullPath().toString(), true);
			this.projectSelection.setText(fileURI.toString());
			if (this.launchConfigTab != null) {
				this.launchConfigTab.updateLaunchConfigurationDialog();
			}
		}
	}
}
