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
 *  Jeremie Tatibouet (CEA LIST) - Alignment of the asynchronous implementation of fUML with the version 1.2 of the standard
 *  
 *****************************************************************************/
package org.eclipse.papyrus.moka.async.fuml.Semantics.CommonBehaviors.Communications;

import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;

/**
 * Interface that need to be implemented by the AsyncEventPool.
 */
public interface IAsyncEventPool {

	/**
	 * Send.
	 *
	 * @param signalInstance
	 *            the signal instance
	 * @return true, if successful
	 */
	public boolean send(EventOccurrence eventOccurrence);

	/**
	 * Gets the next event.
	 *
	 * @return the next event
	 */
	public EventOccurrence getNextEvent();
}
