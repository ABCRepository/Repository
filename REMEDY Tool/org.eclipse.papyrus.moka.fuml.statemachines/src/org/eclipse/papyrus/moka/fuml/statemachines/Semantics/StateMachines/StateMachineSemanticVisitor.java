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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrus.moka.composites.Semantics.CompositeStructures.InvocationActions.CS_SignalInstance;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Object_;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.Execution;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.SignalEventOccurrence;
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.Locus;
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.SemanticVisitor;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.CommonBehavior.CallEventOccurrence;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.CommonBehavior.ChangeEventOccurrence;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.CommonBehavior.EventTriggeredExecution;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.CallEvent;
import org.eclipse.uml2.uml.ChangeEvent;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.SignalEvent;
import org.eclipse.uml2.uml.Trigger;

public abstract class StateMachineSemanticVisitor extends SemanticVisitor {

	// Each semantic visitor for a state-machine know its parent visitor 
	protected SemanticVisitor parent;

	// Each semantic visitor traverse a particular node of a state-machine
	protected NamedElement node;
	
	public NamedElement getNode() {
		return node;
	}

	public void setNode(NamedElement node) {
		this.node = node;
	}

	public SemanticVisitor getParent() {
		return parent;
	}

	public void setParent(SemanticVisitor parent) {
		this.parent = parent;
	}

	public StateMachineSemanticVisitor(){
		this.parent = null;
	}
	
	public List<SemanticVisitor> getContextChain(){
		// Return the hierarchy of visitors that need to be traversed to access
		// the visitor that called context chain. The caller is part of the returned
		// context chain.
		List<SemanticVisitor> contextChain = new ArrayList<SemanticVisitor>();
		if(!(this instanceof ExitPointPseudostateActivation) && !(this instanceof EntryPointPseudostateActivation)){
			contextChain.add(this);
		}
		if(this.parent!=null){
			if(this.parent instanceof StateMachineExecution){
				contextChain.add(this.parent);
			}else{
				contextChain.addAll(((StateMachineSemanticVisitor)this.parent).getContextChain());
			}
		}
		return contextChain;
	}
		
	public Execution getStateMachineExecution(){
		// Return the state-machine execution from which the caller of this operation belongs
		if(this.parent!=null && this.parent instanceof StateMachineExecution){
			return (Execution)this.parent;
		}else{
			return ((StateMachineSemanticVisitor)this.parent).getStateMachineExecution();
		}
	}
	
	public Locus getExecutionLocus(){
		return this.getStateMachineExecution().locus;
	}
	
	public Object_ getExecutionContext(){
		return this.getStateMachineExecution().context;
	}
	
	public boolean isVisitorFor(NamedElement node){
		// A visitor is the interpreter for a model if the node given as parameter is the
		// this model element.
		return this.node == node;
	}
	
	public void activate(){
		// This operation is intended to be overridden by sub-classes. For required sub-classes
		// (e.g., RegionActivation, StateActivation) it will initiate the instantiation phase of
		// child semantic visitors. By default activate does nothing.
		return;
	}
	
	public void activateTransitions(){
		// ActivateTransition is intended to be overridden by sub-classes. It will capture the instantiation
		// of transitions visitors as well as the linking between these visitors and the required vertices
		// activation. By default activate does nothing.
		return;
	}
	
	protected Execution getExecutionFor(Behavior behavior, EventOccurrence eventOccurrence){
		Execution execution = null;
		if(behavior != null){
			Execution originalExecution = this.getExecutionLocus().factory.createExecution(behavior, this.getExecutionContext());
			if(eventOccurrence != null){
				EventTriggeredExecution containerExecution = new EventTriggeredExecution();
				containerExecution.triggeringEventOccurrence = eventOccurrence;
				containerExecution.wrappedExecution = originalExecution;
				containerExecution.context = originalExecution.context;
				execution = containerExecution;
			}else{
				execution = originalExecution;
			}
		}
		return execution;
	}
	
	protected boolean match(EventOccurrence eventOccurrence, List<Trigger> triggers){
		// Check if the event occurrence matches one of the trigger in the list.
		// The matching rule are the following:
		// 		1. If the event occurrence is a signal event occurrence then type
		//		   of the signal must conforms to the type referenced by the event
		//		   In addition, if the trigger defines ports through wich the event occurrence
		//         is allowed to arrive then the arrival port of the event occurrence
		//		   must be one the referenced port.
		// 		2. If the event occurrence is a call event occurrence then the operation
		// 		   that is referenced must be the same than the one specified in the call
		//		   event.
		// NOTE: CallEventOccurrence are not related to an arrival port. This limitation is
		// introduced by a limitation in the current PSCS semantic model.
		// 
		// If a match is found then true is returned, false otherwise.
		boolean match = false;
		int i = 0;
		while(!match && i < triggers.size()){
			Trigger trigger = triggers.get(i);
			if(eventOccurrence instanceof SignalEventOccurrence
					&& trigger.getEvent() instanceof SignalEvent){
				SignalEventOccurrence signalEventOccurrence = (SignalEventOccurrence) eventOccurrence;
				SignalEvent event = (SignalEvent) trigger.getEvent();
				if(event.getSignal() == signalEventOccurrence.signalInstance.type){
					match = true;
				}
				if(match  && trigger.getPorts().size() > 0){
					int j = 0;
					boolean matchingPort = false; 
					while(j < trigger.getPorts().size() & !matchingPort){
						if(((CS_SignalInstance)signalEventOccurrence.signalInstance).interactionPoint.definingPort == trigger.getPorts().get(j)){
							matchingPort = true;
						} 
						j = j + 1;
					}
					if(!matchingPort){
						match = matchingPort;
					}
				}
			}else if(eventOccurrence instanceof CallEventOccurrence
					&& trigger.getEvent() instanceof CallEvent){
				CallEvent event = (CallEvent) trigger.getEvent();
				CallEventOccurrence callEventOccurrence = (CallEventOccurrence) eventOccurrence;
				if(event.getOperation() == callEventOccurrence.execution.operation){
					match = true;
				}
			}
			else if(eventOccurrence instanceof ChangeEventOccurrence && trigger.getEvent() instanceof ChangeEvent){
				ChangeEvent changeEvent = (ChangeEvent)trigger.getEvent();
				ChangeEventOccurrence changeEventOccurrence = (ChangeEventOccurrence)eventOccurrence;
				if(changeEvent.getChangeExpression().equals(changeEventOccurrence.changeExpression)){
					match = true;
				}
			}
			i++;
		}
		return match;
	}
	
	public String toString(){
		return this.node.getName();
	}
}
