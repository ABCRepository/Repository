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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrus.moka.fuml.FUMLExecutionEngine;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.SemanticVisitor;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.TransitionActivation.TransitionMetadata;
import org.eclipse.papyrus.moka.fuml.statemachines.debug.SM_ControlDelegate;
import org.eclipse.uml2.uml.TransitionKind;
import org.eclipse.uml2.uml.Vertex;

public abstract class VertexActivation extends StateMachineSemanticVisitor {
	
	// ACTIVE = the vertex is currently in the state machine configuration
	// IDLE = the vertex is not in the state machine configuration
	public enum StateMetadata{IDLE, ACTIVE}
	
	// Status of the current vertex
	protected StateMetadata status;
	
	// Incoming transitions of that vertex
	protected List<TransitionActivation> incomingTransitionActivations;
	
	// Outgoing transitions of that vertex
	protected List<TransitionActivation> outgoingTransitionActivations;
	
	public VertexActivation(){
		super();
		this.setStatus(StateMetadata.IDLE);
		this.incomingTransitionActivations = new ArrayList<TransitionActivation>();
		this.outgoingTransitionActivations = new ArrayList<TransitionActivation>();
	}
	
	public VertexActivation getParentVertexActivation(){
		// The parent state of a vertex is either a StateMachineExecution or a StateActivation
		RegionActivation regionActivation = (RegionActivation)this.getParent();
		if(regionActivation!=null){
			if(regionActivation.getParent() instanceof StateMachineExecution){
				return null;
			}else{
				return (VertexActivation) regionActivation.getParent();
			}
		}
		return null;
	}
	
	public RegionActivation getOwningRegionActivation(){
		// In general for a vertex activation its owning region activation
		// is its direct parent. Not that is not true for the exit point
		// activation as well as the entry point activation. This operation
		// is therefore overridden in these two context
		return (RegionActivation) this.parent;
	}
	
	public void setStatus(StateMetadata state){
		this.status = state;
	}
	
	public StateMetadata getStatus(){
		return this.status;
	}
	
	public void addIncomingTransition(TransitionActivation activation){
		this.incomingTransitionActivations.add(activation);
	}
	
	public void addOutgoingTransition(TransitionActivation activation){
		this.outgoingTransitionActivations.add(activation);
	}
	
	public List<TransitionActivation> getOutgoingTransitions(){
		return this.outgoingTransitionActivations;
	}
	
	public List<TransitionActivation> getIncomingTransitions(){
		return this.incomingTransitionActivations;
	}

	protected VertexActivation getVertexActivation(Vertex vertex){
		// By default return nothing. Must be overridden by state activation;
		return null;
	}
	
	public final void tagOutgoingTransitions(TransitionMetadata status, boolean staticCheck){
		// Assign the given status (runtime or analysis) to all outgoing transitions of this vertex 
		for(TransitionActivation transitionActivation: this.outgoingTransitionActivations){
			if(staticCheck){
				transitionActivation.analyticalStatus = status;
			}else{
				transitionActivation.status = status;
			}
		}
	}
	
	public final void tagIncomingTransitions(TransitionMetadata status, boolean staticCheck){
		// Assign the given status (runtime or analysis) to all incoming transitions of this vertex
		for(TransitionActivation transitionActivation: this.incomingTransitionActivations){
			if(staticCheck){
				transitionActivation.analyticalStatus = status;
			}else{
				transitionActivation.status = status;
			}
		}
	}
	
	public List<VertexActivation> getAscendingHierarchy(){
		// Provides the hierarchy of state activations starting from the current
		// element. This list is ordered from the innermost element to the outermost element
		List<VertexActivation> hierarchy = new ArrayList<VertexActivation>();
		List<SemanticVisitor> contextChain = this.getContextChain();
		for(SemanticVisitor element : contextChain){
			if(element instanceof StateActivation){
				hierarchy.add((StateActivation)element);
			}
		}
		return hierarchy;
	}
		
	public void enter(TransitionActivation enteringTransition, EventOccurrence eventOccurrence,  RegionActivation leastCommonAncestor){
		// When a vertex is entered its parent may need to be entered as well. Such situation
		// occurs when the parent is not active while there is an attempt to enter the current
		// vertex activation. What is important here is that entry rule is applied recursively
		// until the least common ancestor is reached.
		RegionActivation owningRegionActivation = this.getOwningRegionActivation();
		if(leastCommonAncestor != null && owningRegionActivation != null && leastCommonAncestor != owningRegionActivation){
			VertexActivation vertexActivation = (VertexActivation) owningRegionActivation.getParent();
			if(vertexActivation != null){
				vertexActivation.enter(enteringTransition, eventOccurrence, leastCommonAncestor);
			}
		}
		logger.info(this.getNode().getName()+" => ACTIVE");
		this.setStatus(StateMetadata.ACTIVE);
		this.tagOutgoingTransitions(TransitionMetadata.REACHED, false);
		System.out.println(this.getNode() + " => REACHED");
		FUMLExecutionEngine.eInstance.getControlDelegate().control(this);
	}
	
	public void exit(TransitionActivation exitingTransition, EventOccurrence eventOccurrence, RegionActivation leastCommonAncestor){
		// When a vertex is exited its parent may need to be exited too. Such situation typically
		// occurs when the current vertex is exited through a transition that cross boundaries of
		// the parent state (and maybe also border its own parent). This implies that from the current
		// vertex and until the least common ancestor is reached all states are exited recursively.
		((SM_ControlDelegate)FUMLExecutionEngine.eInstance.getControlDelegate()).inactive(this);
		this.tagIncomingTransitions(TransitionMetadata.NONE, false);
		this.setStatus(StateMetadata.IDLE);
		logger.info(this.getNode().getName()+" => IDLE");
		RegionActivation owningRegionActivation = this.getOwningRegionActivation();
		if(leastCommonAncestor != null && owningRegionActivation != null && leastCommonAncestor != owningRegionActivation){
			VertexActivation vertexActivation = (VertexActivation) owningRegionActivation.getParent();
			if(vertexActivation != null){
				vertexActivation.exit(exitingTransition, eventOccurrence, leastCommonAncestor);
			}
		}
	}
	
	public boolean isActive(){
		// By default is is possible to assess if a vertex is active by checking
		// if its status is ACTIVE. Note this operation is overriden in the context
		// of state activations which require a presence within the state-machine
		// configuration.
		return this.status.equals(StateMetadata.ACTIVE);
	}
	
	public RegionActivation getLeastCommonAncestor(VertexActivation targetVertexActivation, TransitionKind transitionKind){
		// Determine the semantic visitor being the least common ancestor between
		// the current vertex activation and the target vertex activation (provided as
		// a parameter). The analysis is based on a comparative analysis vertices (source and
		// target) hierarchies.
		RegionActivation leastCommonAncestor = null;
		SemanticVisitor sourceHierachyNode =  null;
		SemanticVisitor targetHierarchyNode = null;
		List<SemanticVisitor> sourceHierarchy = this.getContextChain();
		List<SemanticVisitor> targetHierarchy = targetVertexActivation.getContextChain();
		int sourceHierarchyIndex = sourceHierarchy.size();
		int targetHierarchyIndex = targetHierarchy.size();
		// Check if a difference can be found in between the two subsets
		// delimited by the common index. Iterate until the least common
		// ancestor is found or the two subsets have been reviewed
		while(leastCommonAncestor == null && sourceHierarchyIndex > 0 && targetHierarchyIndex > 0){
			sourceHierachyNode = sourceHierarchy.get(sourceHierarchyIndex - 1);
			targetHierarchyNode = targetHierarchy.get(targetHierarchyIndex - 1);
			if(sourceHierachyNode != targetHierarchyNode){
				leastCommonAncestor = this.getRegionActivation(sourceHierachyNode);
			}else{
				sourceHierarchyIndex = sourceHierarchyIndex - 1;
				targetHierarchyIndex = targetHierarchyIndex - 1;
			}
		}
		// It may happen than no difference could found in the hierarchy subsets
		// previously reviewed. This indicate two possible situations:
		// 1. The source and the target are the same.
		// 2. There is containing / container relationship existing between
		//    the source and the target.
		if(leastCommonAncestor == null){
			if(sourceHierarchyIndex == 0 && targetHierarchyIndex == 0){
				leastCommonAncestor = this.getRegionActivation(sourceHierarchy.get(sourceHierarchyIndex + 1));
			}else{
				if(this.getVertexActivation((Vertex)targetVertexActivation.getNode()) != null){
					if(transitionKind == TransitionKind.EXTERNAL_LITERAL){
						leastCommonAncestor = this.getRegionActivation(sourceHierarchy.get(sourceHierarchyIndex));
					}else{
						leastCommonAncestor = this.getRegionActivation(targetHierarchy.get(targetHierarchyIndex - 1));
					}
				}else{
					leastCommonAncestor = this.getRegionActivation(sourceHierarchy.get(sourceHierarchyIndex - 1));
				}
			}
		}
		return leastCommonAncestor;
	}
	
	private RegionActivation getRegionActivation(SemanticVisitor semanticVisitor){
		// If the given semantic visitor is a region activation then this activation
		// is returned. Otherwise if the visitor is a vertex activation  then its
		// parent region activation is returned.
		RegionActivation regionActivation = null; 
		if(semanticVisitor instanceof RegionActivation){
			regionActivation = (RegionActivation) semanticVisitor;
		}else if(semanticVisitor instanceof VertexActivation){
			regionActivation = (RegionActivation)((VertexActivation)semanticVisitor).getParent();
		}
		return regionActivation;
	}
	
	public boolean isEnterable(TransitionActivation enteringTransition, boolean staticCheck){
		// By default a vertex has no prerequisites that need to be full-filled
		// to be entered. Nevertheless some vertex (e.g., join or exit) have such
		// prerequisites. Therefore this method is intended to be overridden in vertex
		// activation sub-classes. 
		return true;
	}
	
	public boolean isExitable(TransitionActivation exitingTransition, boolean staticCheck){
		// By default a vertex has no prerequisites that need to be full-filled to be entered
		// Nevertheless some vertex (e.g., Fork) have such prerequisite. Therefore this method
		// is intended to be overridden in vertex activation sub-classes.
		return true;
	}
	
	public void terminate(){
		// Terminate applied by a vertex activation does nothing by default. However it is intended
		// to be overridden by sub-classe(s)  
		return;
	}
	
	public boolean canPropagateExecution(TransitionActivation enteringTransition, EventOccurrence eventOccurrence , RegionActivation leastCommonAncestor) {
		// The common behavior of all kind of vertices is that when the propagation analysis is done
		// if a the target is a vertex that is nested within a hierarchy then the analysis
		// must be recursively propagated to the parent vertices.
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
}
