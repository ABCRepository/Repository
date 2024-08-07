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

import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.ChoiceStrategy;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.TransitionActivation.TransitionMetadata;

public class JoinPseudostateActivation extends PseudostateActivation {

	@Override
	public boolean isEnterable(TransitionActivation enteringTransition, boolean staticCheck) {
		// Determine if all incoming transitions except this one already have been traversed
		// If this is the case then this node is ready to be entered
		int i = 0;
		boolean isReady = true;
		while (isReady && i < this.incomingTransitionActivations.size()) {
			TransitionActivation transitionActivation = this.incomingTransitionActivations.get(i);
			if (enteringTransition != transitionActivation) {
				isReady = transitionActivation.isTraversed(staticCheck);
			}
			i++;
		}
		return isReady;
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
	public boolean canPropagateExecution(TransitionActivation enteringTransition, EventOccurrence eventOccurrence, RegionActivation leastCommonAncestor) {
		// If the join pseudo-state cannot be entered the propagation stops and returns true. Otherwise
		// to return true it is required that at least on outgoing transition of the pseudo-state 
		// accept the propagation. 
		enteringTransition.analyticalStatus = TransitionMetadata.TRAVERSED;
		boolean propagate = this._canPropagateExecution(enteringTransition, eventOccurrence, leastCommonAncestor);
		if(propagate && this.isEnterable(enteringTransition, true)){
			this.evaluateAllGuards(eventOccurrence);
			if(this.outgoingTransitionActivations.size() > 0){
				propagate = false;
				if(this.fireableTransitions.size() > 0){
					int i = 0;
					while(!propagate && i < this.fireableTransitions.size()){
						propagate = this.fireableTransitions.get(i).canPropagateExecution(eventOccurrence);
						i++;
					}
				}
			}
			this.tagIncomingTransitions(TransitionMetadata.NONE, true);
		}
		return propagate;
	}
	
	@Override
	public void enter(TransitionActivation enteringTransition, EventOccurrence eventOccurrence, RegionActivation leastCommonAncestor) {
		// A join pseudo-state is only allowed to bentered  if all is incoming transitions (except the one
		// currently used to perform the entry) were traversed. When the join pseudo-state is finally entered
		// is traversal is straightforward : one of its fireable transition is traversed. The transition selected
		// to be fired is chosen using the choice strategy.
		super.enter(enteringTransition, eventOccurrence, leastCommonAncestor);
		if(this.fireableTransitions.size() > 0){	
			TransitionActivation selectedTransitionActivation = null;
			if (this.fireableTransitions.size() == 1) {
				selectedTransitionActivation = this.fireableTransitions.get(0);
			}else{
				ChoiceStrategy choiceStrategy = (ChoiceStrategy) this.getExecutionLocus().factory.getStrategy("choice");
				int chosenIndex = choiceStrategy.choose(this.fireableTransitions.size());
				selectedTransitionActivation = this.fireableTransitions.get(chosenIndex - 1);
			}
			selectedTransitionActivation.fire(eventOccurrence);
		}
	}
}
