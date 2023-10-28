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
import org.eclipse.uml2.uml.Vertex;

public class ExternalTransitionActivation extends TransitionActivation {

	protected void exitSource(EventOccurrence eventOccurrence){
		// The exiting phase of the source vertex activation is conditioned both by
		// the prerequisites that apply to leave the source and the prerequisites that
		// apply to enter the target. 
		// 1 - The source can be exited and its target can be entered
		//	 	-> The source is exited using the common ancestor
		// 2 - The source can be exited but is target is not ready to be entered
		//		-> The source is exited but the common ancestor is used. This implies
		//         the exiting phase is not propagated to parent state (if required)
		if(this.vertexSourceActivation.isExitable(this, false)){
			if(this.vertexTargetActivation.isEnterable(this, false)){
				this.vertexSourceActivation.exit(this, eventOccurrence, this.getLeastCommonAncestor());
			}else{
				this.vertexSourceActivation.exit(this, eventOccurrence, null);	
			}
		}
	}
	
	protected void enterTarget(EventOccurrence eventOccurrence) {
		// If the target vertex activation can be entered (i.e., its possible prerequisites
		// are satisfied) then the entering process begins. Note that this process may lead
		// to enter other states based on what is the common ancestor exiting between the
		// the source and the target. Besides the prerequisites imposed by the target vertex
		// activation there are no other constraints to enter the target state
		if(this.vertexTargetActivation.isEnterable(this, false)){
			this.vertexTargetActivation.enter(this, eventOccurrence, this.getLeastCommonAncestor());
		}else{
			if(this.vertexTargetActivation instanceof StateActivation){
				StateActivation targetStateActivation = (StateActivation) this.vertexTargetActivation;
				int i = 0;
				RegionActivation containingRegionActivation = null;
				while(containingRegionActivation == null && i < targetStateActivation.regionActivation.size()){
					RegionActivation currentActivation = targetStateActivation.regionActivation.get(i);
					if(currentActivation.getVertexActivation((Vertex)this.vertexSourceActivation.getNode())!=null){
						containingRegionActivation = currentActivation;
					}
					i++;
				}
				if(containingRegionActivation!=null){
					containingRegionActivation.isCompleted = true;
					if(targetStateActivation.hasCompleted()){
						targetStateActivation.notifyCompletion();
					}
				}
			}
		}
	}
}
