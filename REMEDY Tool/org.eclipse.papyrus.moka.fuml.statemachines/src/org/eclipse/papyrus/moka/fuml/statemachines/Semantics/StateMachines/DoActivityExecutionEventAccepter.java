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
 *  Jeremie Tatibouet (CEA LIST) - Based on Ed Seidewitz remarks
 *
 *****************************************************************************/
package org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines;

import org.eclipse.papyrus.moka.async.fuml.Semantics.CommonBehaviors.Communications.AsyncObjectActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.ArrivalSignal;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventAccepter;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;

public class DoActivityExecutionEventAccepter extends EventAccepter {

	// The doActivity context object in which this accepter is registered
	public DoActivityContextObject context;
	
	// The actual event accepter (i.e., the one registered by the doActivity execution)
	public EventAccepter encapsulatedAccepter;
	
	@Override
	public void accept(EventOccurrence eventOccurrence) {
		// The event accepted through the state-machine event pool leads
		// to the triggering of a RTC step in the context of the doActivity.
		// As this needs to be realized the execution thread of the doActivity
		// the accepted event occurrence is registered at the event pool of the
		// DoActivityContextObjectActivation. This will trigger a new RTC step
		// that will effectively be realized in the DoActivityContextObject and
		// not in the State Machine context.
		((AsyncObjectActivation)this.context.objectActivation).send(eventOccurrence);
		this.context.objectActivation._send(new ArrivalSignal());
	}

	@Override
	public Boolean match(EventOccurrence eventOccurrence) {
		// Simply delegates to the match operation of the encapsulated accepter.
		return this.encapsulatedAccepter.match(eventOccurrence);
	}

}
