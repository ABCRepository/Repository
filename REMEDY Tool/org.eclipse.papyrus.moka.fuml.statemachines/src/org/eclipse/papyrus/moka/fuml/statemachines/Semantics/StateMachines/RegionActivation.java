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

import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.Locus;
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.SemanticVisitor;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Vertex;

public class RegionActivation extends StateMachineSemanticVisitor{
	
	// Semantic visitors for vertices owned by that region
	protected List<VertexActivation> vertexActivations;
	
	// Semantic visitors for transitions owned by that region
	protected List<TransitionActivation> transitionActivations;
	
	// Last known configuration for the region (updated on region exiting)
	// Null if this region has never been entered before.
	protected StateActivation history;
	
	// Is that region completed (e.g., the regions is completed
	// when the final state)
	public boolean isCompleted;
	
	public List<VertexActivation> getVertexActivations() {
		return vertexActivations;
	}

	public List<TransitionActivation> getTransitionActivations() {
		return transitionActivations;
	}
	
	public RegionActivation(){
		this.node = null;
		this.isCompleted = false;
		this.vertexActivations = new ArrayList<VertexActivation>();
		this.transitionActivations = new ArrayList<TransitionActivation>();
	}
	
	@Override
	public boolean isVisitorFor(NamedElement node) {
		// Determine if this visitor is a semantic visitor for the node
		// provided as parameter. This case is verified if the node is the
		// region attached to this semantic visitor or if the node matches
		// a region that is extended (directly or indirectly) by the region
		// attached to this semantic visitor.
		boolean isVisitor = super.isVisitorFor(node);
		if(!isVisitor){
			Region region = ((Region)this.node).getExtendedRegion();
			while(!isVisitor && region != null){
				if(region == node){
					isVisitor = true;
				}else{
					region = region.getExtendedRegion();
				}
			}
		}
		return isVisitor;
	}
	
	private boolean isRedefined(List<Vertex> vertices, Vertex vertex){
		// Ensure that the given vertex is not already redefined by
		// a vertex already present in the list.
		boolean isRedefined = false;
		if(vertex instanceof State){
			// Required check since UML does not allow Vertex to be RedefinableElement
			// FIXME: possible to fix after revision of UML 2.5
			int i = 0;
			while(!isRedefined && i < vertices.size()){
				Vertex currentVertex = vertices.get(i); 
				if(currentVertex instanceof State){
					State state = ((State) currentVertex).getRedefinedState();
					while(!isRedefined && state != null){
						isRedefined = state == vertex;
						state = state.getRedefinedState();
					}
				}
				i++;
			}
		}
		return isRedefined;
	}
	
	public void activate(){
		// In the case where the region extends another region, then the list of
		// state composing the region is computed. This computation is the result
		// of the merge between the region and the extended region. The merge consists
		// in the addition to the set of vertex of all vertices that are not already
		// redefined by another vertex.
		Locus locus = this.getExecutionLocus();
		Region region = (Region)this.getNode();
		VertexActivation activation = null;
		List<Vertex> regionVertices = new ArrayList<Vertex>(region.getSubvertices());
		if(region.getExtendedRegion() != null){
			while(region.getExtendedRegion() != null){
				region = region.getExtendedRegion();
				for(Vertex vertex : region.getSubvertices()){
					if(!this.isRedefined(regionVertices, vertex)){
						regionVertices.add(vertex);
					}
				}
			}
		}
		for(Vertex vertex : regionVertices){
			activation = (VertexActivation)locus.factory.instantiateVisitor(vertex);
			activation.setParent(this);
			activation.setNode(vertex);
			activation.activate();
			this.vertexActivations.add(activation);
		}
	}
	
	private boolean isRedefined(List<Transition> transitions, Transition transition){
		// Ensure that the given transition is not redefined by another transition
		// already included in the transition list.
		boolean isRedefined = false;
		int i = 0;
		while(!isRedefined && i < transitions.size()){
			Transition currentTransition = transitions.get(i).getRedefinedTransition();
			while(!isRedefined && currentTransition != null){
				isRedefined = currentTransition == transition;
				currentTransition = currentTransition.getRedefinedTransition();
			}
			i++;
		}
		return isRedefined;
	}
	
	public void activateTransitions(){
		// Create all semantic visitors for transitions. The source and target
		// vertex activations of each transition activation is then assigned.
		// Only visitors for none redefined transitions are instantiated.
		Region region = (Region)this.getNode();
		VertexActivation sourceActivation = null;
		VertexActivation targetActivation = null;
		TransitionActivation transitionActivation = null;
		StateMachineExecution context = (StateMachineExecution)this.getStateMachineExecution();
		List<Transition> transitions = new ArrayList<>(region.getTransitions());
		if(region.getExtendedRegion() != null){
			while(region.getExtendedRegion() != null){
				region = region.getExtendedRegion();
				for(Transition transition : region.getTransitions()){
					if(!this.isRedefined(transitions, transition)){
						transitions.add(transition);
					}
				}
			}
		}
		for(Transition transition : transitions){
			sourceActivation = context.getVertexActivation(transition.getSource());
			targetActivation = context.getVertexActivation(transition.getTarget());
			transitionActivation = (TransitionActivation) context.locus.factory.instantiateVisitor(transition);
			transitionActivation.setNode(transition);
			transitionActivation.setParent(this);
			transitionActivation.setSourceActivation(sourceActivation);
			transitionActivation.setTargetActivation(targetActivation);
			this.transitionActivations.add(transitionActivation);
			sourceActivation.addOutgoingTransition(transitionActivation);
			targetActivation.addIncomingTransition(transitionActivation);
		}
		for(VertexActivation activation: this.getVertexActivations()){
			activation.activateTransitions();
		}
	}
	
	public VertexActivation getVertexActivation(Vertex vertex){
		// Recursive search through the hierarchy of visitors materializing
		// the sub-set of the state-machine represented by this region.
		// The search is realized in two steps:
		// 	1 - Search a matching activation in set of vertex activations owned by the region activation
		//  2 - If no match, then the search is propagated to each vertex activation owned by the region activation
		// Matching rules (or):
		//  1 - A vertex matches a vertex activation if the node for which this activation is an
		//      an interpreter is the vertex.
		//  2 - A vertex matches a vertex activation if the node for which this activation is an
		//      interpreter redefines the vertex 
		int i = 0;
		VertexActivation activation = null;
		while(activation==null && i < this.vertexActivations.size()){
			if(vertexActivations.get(i).isVisitorFor(vertex)){
				activation = this.vertexActivations.get(i);
			}
			i++;
		}
		i = 0;
		while(activation==null && i < this.vertexActivations.size()){
			if(this.vertexActivations.get(i).isVisitorFor(vertex)){
				activation = this.vertexActivations.get(i);
			}else{
				activation = this.vertexActivations.get(i).getVertexActivation(vertex);
			}
			i++;
		}
		return activation;
	}
	
	protected boolean canPropagateExecution(EventOccurrence eventOccurrence, TransitionActivation enteringTransition){
		// If the region does not provide a initial pseudo-state activation
		// then the execution is considered as being allowed to propagate. The
		// rationale for this choice is that if no initial pseudo state activation
		// is discovered then the region is not considered by the execution.
		// Otherwise if the region has an initial pseudo state activation then
		// the propagation analysis continues through this activation.
		boolean propagate = true;
		if(this.getVertexActivation((Vertex)enteringTransition.vertexTargetActivation.getNode()) == null){
			InitialPseudostateActivation initialNodeActivation = this.getOrigin();
			if(initialNodeActivation != null){
				propagate = initialNodeActivation.canPropagateExecution(enteringTransition, eventOccurrence, null);
			}
		}
		return propagate;
	}
	
	protected InitialPseudostateActivation getOrigin(){
		// Return, if any, the initial pseudo-state activation directly
		// owned by this region.
		int i = 0; 
		InitialPseudostateActivation initialNodeActivation = null;
		while(initialNodeActivation==null && i < this.vertexActivations.size()){
			if(this.vertexActivations.get(i) instanceof InitialPseudostateActivation){
				initialNodeActivation = (InitialPseudostateActivation) this.vertexActivations.get(i);
			}else{
				i++;
			}
		}
		return initialNodeActivation;
	}
	
	protected void enter(TransitionActivation enteringTransition, EventOccurrence eventOccurrence){
		// An implicit entry of a region means the initial transition is searched.
		// If such transition exists then it is fired. An explicit entry as no impact on the region.
		// In case the region is entered implicitly a initial pseudo state shall be found to
		// start its execution. If no such pseudo-state is found and the state containing
		// the region has no other region(s) then the state is treated as a simple leaf state
		InitialPseudostateActivation initialNodeActivation = this.getOrigin();
		if(initialNodeActivation!=null){
			initialNodeActivation.enter(enteringTransition, eventOccurrence, null);
		}else{
			SemanticVisitor parent = this.getParent();
			if(parent != null && parent instanceof StateActivation){
				StateActivation parentState = (StateActivation) parent; 
				parentState.regionActivation.remove(this);
				if(parentState.hasCompleted()){
					parentState.notifyCompletion();
				}
			}
		}
	}
	
	public void exit(TransitionActivation exitingTransition, EventOccurrence eventOccurrence){
		// Exiting a region implies exiting all of is active vertices.
		// Note: there is always a single active vertex for a given region.
		for(VertexActivation vertexActivation: this.getVertexActivations()){
			if(vertexActivation.isActive()){
				vertexActivation.exit(exitingTransition, eventOccurrence, null);
			}
		}
		this.isCompleted = false;
	}
	
	public void terminate(){
		// Capture the semantics related to the termination of a region. Regions typically
		// gets terminated when the state-machine which contains it is gets itself terminated.
		for(int i=0; i < this.vertexActivations.size(); i++){
			this.vertexActivations.get(i).terminate();
		}
		this.vertexActivations.clear();
		this.transitionActivations.clear();
	}
}