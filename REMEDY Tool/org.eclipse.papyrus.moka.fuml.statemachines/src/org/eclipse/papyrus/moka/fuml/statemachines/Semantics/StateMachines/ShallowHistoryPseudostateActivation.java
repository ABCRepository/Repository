/*****************************************************************************
 * Copyright (c) 2016 CEA LIST.
 *
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Jeremie Tatibouet (CEA LIST)
 *
 *****************************************************************************/

package org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines;

import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;

public class ShallowHistoryPseudostateActivation extends HistoryPseudostateActivation {

	@Override
	public void restore(RegionActivation regionActivation, TransitionActivation enteringTransition, EventOccurrence eventOccurrence) {
		// Restore a region activation. The restoration process works as follows:
		// If the region is the one which contains the history then if it exists
		// an history (i.e. the last known configuration) for the region then it
		// its restored.If there is no history, then if it exist an outgoing transition
		// exists for the shallow history then this transition is fired. Otherwise any
		// region that is asked to be restored, is entered using the default entry rule. 
		regionActivation.isCompleted = false;
		if(regionActivation == this.getParent()){
			if(regionActivation.history != null){
				this.restore(regionActivation.history, enteringTransition, eventOccurrence);
			}else{
				if(this.hasDefaultTransition()){
					this.outgoingTransitionActivations.get(0).fire(eventOccurrence);
				}
			}
		}else{
			regionActivation.enter(enteringTransition, eventOccurrence);
		}
	}
}
