/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
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
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrus.moka.launch.MokaLaunchDelegate;

/**
 * An handler for launching the Run from a selected model element
 *
 */
public class DebugAsHandler extends MokaAbstractHandler implements IHandler {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		EObject selectedElement = this.getSelectedElement();
		if (selectedElement != null) {
			String resourceURI = selectedElement.eResource().getURI().toString() ;
			try {
//				ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager() ;
//				ILaunchConfigurationType type = manager.getLaunchConfigurationType("org.eclipse.papyrus.moka.launchConfiguration") ;
//				ILaunchConfigurationWorkingCopy configuration = type.newInstance(null, "New Moka Debug Configuration") ;
//				configuration.setAttribute(MokaLaunchDelegate.URI_ATTRIBUTE_NAME, resourceURI);
//				configuration.setAttribute(MokaLaunchDelegate.FRAGMENT_ATTRIBUTE_NAME, selectedElement.eResource().getURIFragment(selectedElement));
//				configuration.doSave() ;
//				DebugUITools.launch(configuration, "debug");
				
				
				ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager() ;
				ILaunchConfigurationType type = manager.getLaunchConfigurationType("org.eclipse.papyrus.moka.launchConfiguration") ;
				ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
				if(configurations.length == 0){
					ILaunchConfigurationWorkingCopy configuration = type.newInstance(null, "New Moka Debug Configuration") ;
					configuration.setAttribute(MokaLaunchDelegate.URI_ATTRIBUTE_NAME, resourceURI);
					configuration.setAttribute(MokaLaunchDelegate.FRAGMENT_ATTRIBUTE_NAME, selectedElement.eResource().getURIFragment(selectedElement));
					configuration.doSave() ;
					DebugUITools.launch(configuration, "debug");
				}
				else{
					DebugUITools.launch(configurations[0], "debug");
				}
				
				
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
