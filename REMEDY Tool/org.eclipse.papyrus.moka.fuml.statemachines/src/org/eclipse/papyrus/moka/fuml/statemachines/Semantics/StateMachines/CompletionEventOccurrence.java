/*****************************************************************************
 * Copyright (c) 2015 CEA LIST and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   CEA LIST - Initial API and implementation
 *   
 *****************************************************************************/

package org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines;

import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;

public class CompletionEventOccurrence extends EventOccurrence {
	
	/*
	 * A completion event is a specific kind of event occurrence.
	 * 
	 * It is placed in the event pool of an active object under the following circumstances:
	 * 	
	 * 1 - The state is simple. As soon as its entry behavior as well as its doActivity behavior 
	 *      finished a completion event is generated.
	 *      
	 *  2 - The state is composite. All internal activities (e.g., entry and doActivity Behaviors) have
	 *      completed execution, and all its orthogonal Regions have reached a FinalState
	 *      
	 */
	
	// A reference to the state from which the completion event was generated
	public StateActivation stateActivation;

}
