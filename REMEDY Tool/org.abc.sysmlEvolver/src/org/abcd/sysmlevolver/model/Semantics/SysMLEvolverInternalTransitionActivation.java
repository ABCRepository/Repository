package org.abcd.sysmlevolver.model.Semantics;

import java.util.List;

import org.abc.constraint.ConstraintInstance;
import org.abc.constraint.ConstraintInstanceFactory;
import org.abcd.sysmlevolver.ExecutionParameters;
import org.abcd.sysmlevolver.debug.TestControlDelegate;
import org.abcd.sysmlevolver.model.loci.ActiveObjectActivation;
import org.abcd.sysmlevolver.test.log.TestLogger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrus.moka.fuml.FUMLExecutionEngine;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.FinalStateActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.InternalTransitionActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateConfiguration;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateMachineExecution;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.VertexActivation;
import org.eclipse.uml2.uml.ChangeEvent;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Transition;

public class SysMLEvolverInternalTransitionActivation extends InternalTransitionActivation {
	
	@Override
	public boolean evaluateGuard(EventOccurrence eventOccurrence){
		
		Transition transition = (Transition) this.node;
		Constraint guard = transition.getGuard();
		if(guard != null){
			ConstraintInstance guardEvaluation = ConstraintInstanceFactory.instance().createConstraintInstance(guard, eventOccurrence);
			return guardEvaluation.evaluate();
		}
		else{
			return true;
		}
		
	}
	
	private void updateConstraintList(SysMLEvolverStateActivation stateActivation){
		
		StateMachineExecution execution = (StateMachineExecution)this.getStateMachineExecution();
		StateConfiguration root = execution.getConfiguration().getRoot();
		
		StateConfiguration configuration = getStateConfiguration(root, stateActivation);
		if(configuration != null){
			removeConstraint(configuration);
		}
	}
	
	private void removeConstraint(StateConfiguration configuration){
		VertexActivation activation = configuration.getVertexActivation();
		
		
		for(StateConfiguration child : configuration.getChildren()){
			removeConstraint(child);
		}
	}
	
	private StateConfiguration getStateConfiguration(StateConfiguration configuration, SysMLEvolverStateActivation stateActivation){
		
		if(configuration.getVertexActivation().equals(stateActivation)){
			return configuration;
		}
		
		for(StateConfiguration child : configuration.getChildren()){
			StateConfiguration res = getStateConfiguration(child, stateActivation);
			if(res != null){
				return res;
			}
		}
		
		return null;
	}
	
	@Override
	protected void exitSource(EventOccurrence eventOccurrence) {
		
		
		if(vertexSourceActivation instanceof SysMLEvolverStateActivation){
			updateConstraintList((SysMLEvolverStateActivation)vertexSourceActivation);
		}
		
		super.exitSource(eventOccurrence);
		TestLogger.log(this.vertexSourceActivation, true);
		
		NamedElement node = this.vertexSourceActivation.getNode();
		if(!(node instanceof State)){
			return;
		}
		
	}
	
	@Override
	protected void enterTarget(EventOccurrence eventOccurrence) {
		
		wait4stateUpdate();
		
		TestLogger.log(this.vertexTargetActivation, true);
		return;
	}
	
	private void wait4stateUpdate(){
		
		if(ExecutionParameters.simulating){
			return;
		}
		
		if(vertexTargetActivation instanceof FinalStateActivation){
			return;
		}
		
		if(vertexTargetActivation instanceof StateActivation){
			
			ConstraintInstance invariant = ((SysMLEvolverStateActivation)vertexTargetActivation).stateInvariant;
			if(invariant == null){
				return;
			}
			
			while(true){
				if(invariant.evaluate()){ 
					ActiveObjectActivation activation = (ActiveObjectActivation)((ActiveObject)this.getExecutionContext()).objectActivation;
					activation.setTraversing(false);
					break; 
				}
				else{
					ActiveObjectActivation activation = (ActiveObjectActivation)((ActiveObject)this.getExecutionContext()).objectActivation;
					activation.setTraversing(true);
					synchronized(activation){
						try {
							activation.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
			}
			
		}
	}
}
