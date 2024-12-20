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
 *  Jeremie Tatibouet (CEA LIST)
 *
 *****************************************************************************/
package org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines;

import java.util.Iterator;

import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;

public class ForkPseudostateActivation extends PseudostateActivation {

	@Override
	public boolean isExitable(TransitionActivation exitingTransition, boolean staticCheck) {
		// The Fork node activation can only be exited when all of its outgoing transitions
		// (expect the current "exitingTransition")previously fired.
		int i = 0;
		boolean isExitable = true;
		while(isExitable && i < this.outgoingTransitionActivations.size()){
			TransitionActivation transitionActivation = this.outgoingTransitionActivations.get(i);
			if(transitionActivation != exitingTransition){
				isExitable = transitionActivation.isTraversed(staticCheck);
			}
			i++;
		}
		return isExitable;
	}
	
	private boolean _canPropagateExecution(TransitionActivation enteringTransition, EventOccurrence eventOccurrence, RegionActivation leastCommonAncestor){
		// Convenience method. Java does no allow a call to an explicit super class method. For documentation
		// developpers must have a look to: VertexActivation::canPropagateExecution(...)
		boolean propagate = true;
		if(leastCommonAncestor != null){
			RegionActivation parentRegionActivation = this.getOwningRegionActivation();
			if(leastCommonAncestor!=parentRegionActivation){
				VertexActivation vertexActivation = (VertexActivation) parentRegionActivation.getParent();
				if(vertexActivation != null){
					propagate = vertexActivation.canPropagateExecution(enteringTransition, eventOccurrence, leastCommonAncestor);
				}
			}
		}
		return propagate;
	}
	
	@Override
	public boolean canPropagateExecution(TransitionActivation enteringTransition, EventOccurrence eventOccurrence, RegionActivation leastCommonAncestor){
		// Static analysis is propagated through the parent. If the propagation is accepted by the parent, then it is evaluated
		// for this pseudo-state. A fork pseudo-state can propagate the execution if all of its outgoing transitions can
		// propagate the execution. Note that there is no guard evaluation. This is normal since outgoing transitions of
		// a fork cannot have guards.
		boolean propagate = this._canPropagateExecution(enteringTransition, eventOccurrence, leastCommonAncestor);
		if(propagate && this.isEnterable(enteringTransition, true)){
			int i = 0;
			while(propagate && i < this.outgoingTransitionActivations.size()){
				propagate = this.outgoingTransitionActivations.get(i).canPropagateExecution(eventOccurrence);
				i++;
			}
		}
		return propagate;
	}
	
	@Override
	public void enter(TransitionActivation enteringTransition, EventOccurrence eventOccurrence, RegionActivation leastCommonAncestor) {
		// Fires all outgoing transitions of the for **concurrently**
		// Transitions outgoing from a fork are not guarded nor triggered
		// If required parent state is entered first (the rule applies recursively)
		super.enter(enteringTransition, eventOccurrence, leastCommonAncestor);
		for(Iterator<TransitionActivation> fireableTransitionsIterator = this.outgoingTransitionActivations.iterator(); fireableTransitionsIterator.hasNext();){
			fireableTransitionsIterator.next().fire(eventOccurrence);
		}
	}
	
}
