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
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.ChoiceStrategy;

public class JunctionPseudostateActivation extends ConditionalPseudostateActivation{
	
	@Override
	public void enter(TransitionActivation enteringTransition, EventOccurrence eventOccurrence, RegionActivation leastCommonAncestor) {
		// When entered the junction pseudo-state the set of fireable transition (calculated during static
		// analysis) is used to determine which transition will actually be fired. The transition selected
		// to be fired is determined using the choice semantic strategy.
		super.enter(enteringTransition, eventOccurrence, leastCommonAncestor);
		TransitionActivation selectedTransition = null;
		if(this.fireableTransitions.size() == 1){
			selectedTransition = this.fireableTransitions.get(0);
		}else{
			ChoiceStrategy choiceStrategy = (ChoiceStrategy) this.getExecutionLocus().factory.getStrategy("choice");
			int index = choiceStrategy.choose(this.fireableTransitions.size()) - 1;
			selectedTransition = this.fireableTransitions.get(index);
		}
		if(selectedTransition != null){
			selectedTransition.fire(eventOccurrence);
		}
	}
	
}
