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


import static org.eclipse.papyrus.moka.fuml.statemachines.Activator.logger;

import org.eclipse.papyrus.moka.fuml.FUMLExecutionEngine;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.Execution;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.SignalEventOccurrence;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.CommonBehavior.CallEventOccurrence;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.CommonBehavior.ChangeEventOccurrence;
import org.eclipse.papyrus.moka.fuml.statemachines.debug.SM_ControlDelegate;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.ValueSpecification;

public abstract class TransitionActivation extends StateMachineSemanticVisitor {
	
	// Provide the status of a specific transition
	// NONE: The transition exists, however its source state was is not yet reached 
	// REACHED: The source vertex of the transition is reached
	// TRAVERSED: The transition was executed
	public enum TransitionMetadata{NONE, REACHED, TRAVERSED}
	
	// The source activation of this transition activation
	protected VertexActivation vertexSourceActivation;
	
	// The target activation of this transition activation
	protected VertexActivation vertexTargetActivation;
	
	// The runtime status (NONE, REACHED, TRAVERSED) of the transition
	protected TransitionMetadata status;
	
	// Least common ancestor of the source and the target. This is materialized
	// by the region activation that is the common ancestor of the source and the target. 
	private RegionActivation leastCommonAncestor;
	
	// The static status (NONE, REACHED, TRAVERSED) of the transition
	protected TransitionMetadata analyticalStatus;
	
	// The last event occurrence used during static analysis.
	private EventOccurrence lastTriggeringEventOccurrence;
	
	// The last verdict when the execution was propagated over this transition.
	private boolean lastPropagation;
	
	public TransitionMetadata getStatus() {
		return status;
	}

	public void setStatus(TransitionMetadata state) {
		this.status = state;
	}

	public TransitionActivation(){
		super();
		this.status = TransitionMetadata.NONE;
		this.analyticalStatus = TransitionMetadata.NONE;
		this.leastCommonAncestor = null;
		this.lastTriggeringEventOccurrence = null;
		this.lastPropagation = false;
	}

	public boolean isReached(boolean staticCheck){
		/// Convenience operation which returns true if the status of this transition
		// is REACHED; false otherwise.
		boolean reached = true;
		if(staticCheck){
			reached = this.analyticalStatus.equals(TransitionMetadata.REACHED);
		}else{
			reached = this.status.equals(TransitionMetadata.REACHED);
		}
		return reached;
	}
	
	public boolean isTraversed(boolean staticCheck){
		// Convenience operation which returns true if the status of this transition
		// is TRAVERSED; false otherwise.
		boolean traversed = true;
		if(staticCheck){
			traversed = this.analyticalStatus.equals(TransitionMetadata.TRAVERSED);
		}else{
			traversed = this.status.equals(TransitionMetadata.TRAVERSED);
		}
		return traversed;
	}
	
	@Override
	public boolean isVisitorFor(NamedElement node) {
		// Determine if this visitor is a semantic visitor for the node
		// provided as a parameter.This case is verified if the node is
		// the same as the transition attached to the semantic visitor or
		// if the node matches a transition that is redefined (directly or
		// indirectly) by the transition attached to this semantic visitor.
		boolean isVisitor = super.isVisitorFor(node);
		if(!isVisitor){
			Transition transition = ((Transition)this.node).getRedefinedTransition();
			while(!isVisitor && transition != null){
				if(transition == node){
					isVisitor = true;
				}else{
					transition = transition.getRedefinedTransition();
				}
			}
		}
		return isVisitor;
	}
	
	public boolean isTriggered(){
		return !((Transition)this.node).getTriggers().isEmpty();
	}
	
	public boolean isGuarded(){
		return ((Transition)this.node).getGuard()!=null;
	}
	
	public VertexActivation getSourceActivation() {
		return vertexSourceActivation;
	}

	public void setSourceActivation(VertexActivation vertexSourceActivation) {
		this.vertexSourceActivation = vertexSourceActivation;
	}

	public VertexActivation getTargetActivation() {
		return vertexTargetActivation;
	}

	public void setTargetActivation(VertexActivation vertexTargetActivation) {
		this.vertexTargetActivation = vertexTargetActivation;
	}
	
	public boolean evaluateGuard(EventOccurrence eventOccurrence){
		
		// Evaluate the guard specification thanks to an evaluation.
		// The evaluation does not presume of the type of the guard specification.
		boolean result = true;  
		Transition transition = (Transition) this.node;
		if (transition.getGuard() != null) {
			ValueSpecification specification = transition.getGuard().getSpecification() ;
			if(specification!=null){
				
				// TODO 
//				Evaluation evaluation = this.getExecutionLocus().factory.createEvaluation(specification);
//				if (specification instanceof OpaqueExpression) {
//					((SM_OpaqueExpressionEvaluation)evaluation).context = this.getExecutionContext() ;
//					((SM_OpaqueExpressionEvaluation)evaluation).initialize(eventOccurrence);
//				}
//				if(evaluation!=null){
//					BooleanValue evaluationResult = (BooleanValue)evaluation.evaluate() ;
//					result = evaluationResult.value ;
//				}
			}
			
		}
		return result;
	}
	
	public boolean hasTrigger(EventOccurrence eventOccurrence){
		// Return true if the event occurrence matches a trigger of this transition.
		// false otherwise.
		return this.match(eventOccurrence, ((Transition)this.node).getTriggers());
	}
	
	public boolean canFireOn(EventOccurrence eventOccurrence){
		// A transition is can fire when:
		//
		// A completion event is being dispatched and this transition has no trigger
		// but its eventual guard evaluates to true. Note: the scope of a completion
		// event is the state from which it was generated
		//
		// A signal event is being dispatched and this transition has a trigger
		// that matches the signal and its eventual guard evaluates to true
		boolean reactive = true;
		if(eventOccurrence instanceof CompletionEventOccurrence){
			reactive = !this.isTriggered() &&
						this.getSourceActivation()==((CompletionEventOccurrence)eventOccurrence).stateActivation &&
						this.evaluateGuard(eventOccurrence) &&
						this.canPropagateExecution(eventOccurrence);
		}else if(eventOccurrence instanceof SignalEventOccurrence || eventOccurrence instanceof CallEventOccurrence ||
				eventOccurrence instanceof ChangeEventOccurrence){
			reactive = this.hasTrigger(eventOccurrence) && 
					   this.evaluateGuard(eventOccurrence) &&
					   this.canPropagateExecution(eventOccurrence);
		}else{
			reactive = false;
		}
		return reactive;
	}
	
	public boolean canPropagateExecution(EventOccurrence eventOccurrence){
		// Evaluate the possibility to propagate the static analysis through this transition activation.
		// Two situations can occur:
		// 1. The transition has already been "traversed" with using the same event occurrence. This means
		//    we already know the execution can be propagated through the transiton activation. Hence true
		//    is returned and the propagation stops.
		// 2. The transition has not already been "traversed" using this event occurrence. The consequence
		//    is that the analysis is propagated through the target vertex activation. 
		boolean propagate = true;
		if(this.lastTriggeringEventOccurrence != eventOccurrence){
			propagate = this.vertexTargetActivation.canPropagateExecution(this, eventOccurrence, this.getLeastCommonAncestor());
			this.lastTriggeringEventOccurrence = eventOccurrence;
			this.lastPropagation = propagate;
		}else{
			propagate = this.lastPropagation;
		}
		return propagate;
	}
	
	public void executeEffect(EventOccurrence eventOccurrence){
		// Execute the effect that is on the transition if it exists one
		Transition transition = (Transition) this.getNode();
		Execution execution = this.getExecutionFor(transition.getEffect(), eventOccurrence);
		if(execution!=null){
			execution.execute();
		}
	}
	
	public void fire(EventOccurrence eventOccurrence){
		// The fire sequence is broken into the following set of actions
		// 1 - Exit the source (depends on the kind of transition that is currently used)
		// 2 - Execute the effect (if one exists for that transition)
		// 3 - Enter the target (depends on the kind of transition that is currently used)
		this.exitSource(eventOccurrence);
		FUMLExecutionEngine.eInstance.getControlDelegate().control(this); 
		this.executeEffect(eventOccurrence);
		((SM_ControlDelegate)FUMLExecutionEngine.eInstance.getControlDelegate()).inactive(this); 
		this.setStatus(TransitionMetadata.TRAVERSED);
		
		System.out.println(this.getNode() + " => TRAVERSED");
		
		logger.info(this.getNode().getName()+" => TRAVERSED");
		this.enterTarget(eventOccurrence);
	}
	

	protected RegionActivation getLeastCommonAncestor(){
		// Return the common ancestor of the source and the target. This common ancestor is
		// a region activation
		if(this.vertexSourceActivation.getParentVertexActivation()!=this.vertexTargetActivation.getParentVertexActivation()){
			if(this.leastCommonAncestor==null){
				this.leastCommonAncestor = this.vertexSourceActivation.getLeastCommonAncestor(this.vertexTargetActivation, ((Transition)this.getNode()).getKind());
			}
		}
		return this.leastCommonAncestor;
	}
	
	/*
	 * This operation is intended to be implemented by sub-classes.
	 * Sub-classes capture how the source vertex activation must be exited. 
	 */
	protected abstract void exitSource(EventOccurrence eventOccurrence);
	
	/*
	 * This operation is intended to be implemented by sub-classes.
	 * Sub-classes capture how the target vertex activation must be entered.
	 */
	protected abstract void enterTarget(EventOccurrence eventOccurrence);
	
	public String toString(){
		String representation = "["+this.getSourceActivation()+"] -> ["+this.getTargetActivation()+"] (";
		if(this.isReached(false)){
			representation += "REACHED";
		}else if(this.isTraversed(false)){
			representation += "TRAVERSED";
		}else{
			representation += "NONE";
		}
		return representation +")";
	}
}
