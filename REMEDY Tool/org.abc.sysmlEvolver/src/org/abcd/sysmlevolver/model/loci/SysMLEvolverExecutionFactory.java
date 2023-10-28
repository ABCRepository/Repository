package org.abcd.sysmlevolver.model.loci;

import org.abcd.sysmlevolver.model.Semantics.SysMLEvolverExternalTransitionActivation;
import org.abcd.sysmlevolver.model.Semantics.SysMLEvolverInternalTransitionActivation;
import org.abcd.sysmlevolver.model.Semantics.SysMLEvolverLocalTransitionActivation;
import org.abcd.sysmlevolver.model.Semantics.SysMLEvolverStateActivation;
import org.abcd.sysmlevolver.model.Semantics.SysMLEvolverStateMachineExecution;
import org.abcd.sysmlevolver.model.actions.SUTCallActionActivation;
import org.abcd.sysmlevolver.model.actions.SUTSyncActionActivation;
import org.abcd.sysmlevolver.model.actions.UpdateFeatureActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Object_;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.Execution;
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.SemanticVisitor;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.Loci.SM_ExecutionFactory;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.FinalStateActivation;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.BroadcastSignalAction;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.FinalState;
import org.eclipse.uml2.uml.OpaqueAction;
import org.eclipse.uml2.uml.OpaqueBehavior;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Transition;

public class SysMLEvolverExecutionFactory extends SM_ExecutionFactory {
	
	@Override
	public Execution createExecution(Behavior behavior, Object_ context) {
		// Create an execution object for a given behavior.
		// The execution will take place at the locus of the factory in the
		// given context.
		// If the context is empty, the execution is assumed to provide its own
		// context.
		Execution execution;
		if (behavior instanceof OpaqueBehavior) {
			execution = this.instantiateOpaqueBehaviorExecution((OpaqueBehavior) behavior);
		} else {
			execution = (Execution) this.instantiateVisitor(behavior);
			execution.types.add(behavior);
			execution.createFeatureValues();
		}
		this.locus.add(execution);
		if (context == null) {
			execution.context = execution;
		} else {
			execution.context = context;
		}
		
		
		
		return execution;
	}
	
	@Override
	public SemanticVisitor instantiateVisitor(Element element) {
		
		if(element instanceof StateMachine){
			return new SysMLEvolverStateMachineExecution();
		}
		else{
			SemanticVisitor visitor = null ;
			if (element instanceof Transition) {
				Transition transition = (Transition) element;
				switch(transition.getKind()){
					case EXTERNAL_LITERAL: visitor = new SysMLEvolverExternalTransitionActivation(); break;
					case INTERNAL_LITERAL: visitor = new SysMLEvolverInternalTransitionActivation(); break;
					case LOCAL_LITERAL: visitor = new SysMLEvolverLocalTransitionActivation(); break;
				}
			}
			else if (element instanceof State) {
				if(element instanceof FinalState){
					visitor = new FinalStateActivation();
				}else{
					visitor = new SysMLEvolverStateActivation() ; 
					
				}
			}
			
			
			
			
			if(visitor != null){
				return visitor;
			}
			else{
				return super.instantiateVisitor(element);
			}
		}
		
	}

}
