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
import org.eclipse.uml2.uml.Expression;
import org.eclipse.uml2.uml.Transition;

public abstract class ConditionalPseudostateActivation extends PseudostateActivation{
	
	protected final String ELSE_OPERATOR = "else";
	
	protected boolean isElseTransition(TransitionActivation transitionActivation){
		// Determine if the given transition materialize the else branch of a conditional pseudo state.
		// A transition materializes an else branch since it has a guard which specification is
		// an Expression that has no operand(s) and whose symbol is "else"
		boolean isElse = false;
		if(transitionActivation!=null){
			Transition transition = (Transition)transitionActivation.getNode();
			if(transition.getGuard()!=null && transition.getGuard().getSpecification() instanceof Expression){
				Expression expression = (Expression) transition.getGuard().getSpecification();
				isElse = expression.getOperands().isEmpty() && expression.getSymbol() !=null && expression.getSymbol().equals(ELSE_OPERATOR);
			}
		}
		return isElse;
	}
	
	protected void evaluateAllGuards(EventOccurrence eventOccurrence){
		// Evaluate all guards of transitions outgoing this conditional pseudo-state.
		// and populate the set of fireable transitions. Note that this set is cleared
		// before each evaluation.
		this.fireableTransitions.clear();
		TransitionActivation elseTransitionActivation = null;
		for(int i=0; i < this.outgoingTransitionActivations.size(); i++){
			TransitionActivation transitionActivation = this.outgoingTransitionActivations.get(i);
			if(this.isElseTransition(transitionActivation)){
				elseTransitionActivation = transitionActivation;
			}else{
				if(transitionActivation.evaluateGuard(eventOccurrence)){
					this.fireableTransitions.add(transitionActivation);
				}
			}
		}
		if(this.fireableTransitions.isEmpty() && elseTransitionActivation != null){
			this.fireableTransitions.add(elseTransitionActivation);
		}
	}

}
