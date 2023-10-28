package org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines;

import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.Execution;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;

public class TerminatePseudostateActivation extends PseudostateActivation {

	public void enter(TransitionActivation enteringTransition, EventOccurrence eventOccurrence, RegionActivation leastCommonAncestor) {
		// When a terminate pseudo state is entered the state-machine executing terminates its execution.
		// This termination occurs without exiting "exit" behaviors of state(s) that are currently
		// registered in the state-machine configuration.
		// 
		// Note: the termination of the current state-machine execution leads
		// to the destruction of its context object. This means the object
		// will be stopped and consequently no any other event will be dispatched
		// and the object will finally be removed from the locus.
		super.enter(enteringTransition, eventOccurrence, leastCommonAncestor);
		Execution stateMachineExecution = this.getStateMachineExecution();
		stateMachineExecution.terminate();
		stateMachineExecution.context.destroy();
		super.exit(null, null, null);
	}
}
