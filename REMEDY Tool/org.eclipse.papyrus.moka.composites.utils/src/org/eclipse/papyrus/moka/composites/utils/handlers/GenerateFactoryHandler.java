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
package org.eclipse.papyrus.moka.composites.utils.handlers;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.uml2.uml.Class;


public class GenerateFactoryHandler extends AbstractCompositeUtilsHandler {

	@Override
	public RecordingCommand getUpdateCommand(Class context, TransactionalEditingDomain domain) {
		return new GenerateFactoryCommand(context, domain);
	}

	/**
	 * Command that generate factory for a given class.
	 *
	 * @see Utils.getFactory
	 */
	protected class GenerateFactoryCommand extends RecordingCommand {

		protected Class context;

		public GenerateFactoryCommand(Class context, TransactionalEditingDomain domain) {
			super(domain);
			this.context = context;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand#doExecuteWithResult(org.eclipse.core.runtime.IProgressMonitor
		 * , org.eclipse.core.runtime.IAdaptable)
		 */
		@Override
		protected void doExecute() {
			Utils.getFactory(context);
		}
	}
}
