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

package org.eclipse.papyrus.moka.fuml.standardlibrary.library.io;

import java.util.List;

import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.StringValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.Execution;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.papyrus.moka.fuml.debug.Debug;
import org.eclipse.papyrus.moka.fuml.registry.service.framework.AbstractService;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Operation;

public class StandardOutputChannelImpl extends AbstractService {

	protected static final String CONSOLE_NAME = "fUML Console";

	protected static IOConsole console = null;

	protected IOConsoleOutputStream out = null;

	public static IOConsole getConsole() {
		if (console == null) {
			console = new IOConsole(CONSOLE_NAME, null);
			IConsoleManager conMan = ConsolePlugin.getDefault().getConsoleManager();
			conMan.addConsoles(new IConsole[] { console });
		}
		return console;
	}

	public StandardOutputChannelImpl(Class service) {
		super(service);
		this.out = getConsole().newOutputStream();
	}

	@Override
	public Execution dispatch(Operation operation) {
		if (operation.getName().equals("writeLine")) {
			return new WriteLineExecution(operation);
		} else if (operation.getName().equals("write")) {
			return new Write(operation);
		}
		// TODO complete with other operations
		return null;
	}

	protected class WriteLineExecution extends AbstractService.ServiceOperationExecution {

		public WriteLineExecution(Operation operation) {
			super(operation);
		}

		@Override
		public Value new_() {
			return new WriteLineExecution(operation);
		}

		@Override
		public void doBody(List<ParameterValue> inputParameters, List<ParameterValue> outputParameters) {
			// Supposed to have only one input argument, corresponding to parameter 'value'
			try {
				String message = "";
				message = ((StringValue) inputParameters.get(0).values.get(0)).value;
				out.write(message + "\n");
				out.flush();
				// This implementation does not produce errorStatus information.
			} catch (Exception e) {
				Debug.println("An error occured during the execution of writeLine " + e.getMessage());
			}
		}
	}

	protected class Write extends AbstractService.ServiceOperationExecution {

		protected Operation operation;

		public Write(Operation operation) {
			super(operation);
		}

		@Override
		public Value new_() {
			return new Write(operation);
		}

		@Override
		public void doBody(List<ParameterValue> inputParameters, List<ParameterValue> outputParameters) {
			// Supposed to have only one input argument, corresponding to parameter 'value'
			try {
				String message = inputParameters.get(0).values.get(0).toString();
				out.write(message);
				out.flush();
				// This implementation does not produce errorStatus information.
			} catch (Exception e) {
				Debug.println("An error occured during the execution of write " + e.getMessage());
			}
		}
	}
}
