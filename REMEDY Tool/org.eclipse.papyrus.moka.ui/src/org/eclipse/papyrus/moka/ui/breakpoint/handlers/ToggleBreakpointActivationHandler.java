/*****************************************************************************
 * Copyright (c) 2013 CEA LIST.
 *
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  CEA LIST - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.moka.ui.breakpoint.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.papyrus.infra.core.Activator;
import org.eclipse.papyrus.moka.MokaConstants;
import org.eclipse.papyrus.moka.debug.MokaBreakpoint;

/**
 * An handler for managing activation/deactivation of a Moka Breakpoint
 *
 */
public class ToggleBreakpointActivationHandler extends MokaAbstractHandler implements IHandler {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		EObject selectedElement = this.getSelectedElement();
		String selectedElementURI = EcoreUtil.getURI(selectedElement).toString();
		if (selectedElement != null) {
			IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
			IBreakpoint[] breakpoints = breakpointManager.getBreakpoints(MokaConstants.MOKA_DEBUG_MODEL_ID);
			IBreakpoint alreadyDefinedBreakpoint = null;
			for (int i = 0; i < breakpoints.length && alreadyDefinedBreakpoint == null; i++) {
				MokaBreakpoint breakpoint = (MokaBreakpoint) breakpoints[i];
				EObject eObjectOfBreakpoint = breakpoint.getModelElement();
				if (eObjectOfBreakpoint != null && EcoreUtil.getURI(eObjectOfBreakpoint).toString().equals(selectedElementURI)) {
					alreadyDefinedBreakpoint = breakpoint;
				}
			}

			if (alreadyDefinedBreakpoint != null) {
				try {
					boolean enabledOrDisabled = !alreadyDefinedBreakpoint.isEnabled();
					alreadyDefinedBreakpoint.setEnabled(enabledOrDisabled);
					DebugPlugin.getDefault().getBreakpointManager().fireBreakpointChanged(alreadyDefinedBreakpoint);
				} catch (CoreException e) {
					Activator.log.error(e);
				}
			}
			return null;
		}

		return null;
	}

}
