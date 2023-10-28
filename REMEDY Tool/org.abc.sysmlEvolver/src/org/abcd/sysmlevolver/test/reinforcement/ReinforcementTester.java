package org.abcd.sysmlevolver.test.reinforcement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.abcd.sysmlevolver.model.Semantics.SysMLEvolverStateActivation;
import org.abcd.sysmlevolver.model.loci.ActiveObjectActivation;
import org.abcd.sysmlevolver.test.invocation.Invocation;
import org.abcd.sysmlevolver.test.sut.SUTObject;
import org.abcd.sysmlevolver.test.utils.FaultySenario;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateConfiguration;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateMachineExecution;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.TransitionActivation;
import org.eclipse.uml2.uml.CallEvent;
import org.eclipse.uml2.uml.ChangeEvent;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Event;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Trigger;

public abstract class ReinforcementTester {

	protected int maxEpochNum;

	public void init(int maxEpochNum) {
		this.maxEpochNum = maxEpochNum;
	}

	public abstract void logParameters();

	public abstract boolean episodeStart(SUTObject sut, List<FaultySenario> faultySenarios);

	public abstract Invocation nextEpoch(SUTObject sut, double reward);

	public abstract void episodeFinish(double fragility);

	public abstract String toString(int nIteration);

	public boolean areAllTransitionsCovered() {
		return true;
	}

	protected List<Invocation> getCandidateInvocations(SUTObject sut) {

		List<Invocation> validInvocations = new ArrayList<Invocation>();

		Map<String, ActiveObjectActivation> activeObjectActivations = sut.getActiveObjectActivations();

		for (ActiveObjectActivation objectActivation : activeObjectActivations.values()) {

			StateMachineExecution execution = (StateMachineExecution) (objectActivation.classifierBehaviorInvocations
					.get(0).execution);
			validInvocations.addAll(getCandidateInvocations(objectActivation, execution.getConfiguration().getRoot()));
		}

		return validInvocations;
	}

	protected List<Invocation> getCandidateInvocations(ActiveObjectActivation objectActivation,
			StateConfiguration configuration) {

		List<Invocation> validInvocations = new ArrayList<Invocation>();
		for (StateConfiguration child : configuration.getChildren()) {
			validInvocations.addAll(getCandidateInvocations(objectActivation, child));
		}

		SysMLEvolverStateActivation stateActivation = (SysMLEvolverStateActivation) configuration.getVertexActivation();
		if (stateActivation == null) {
			return validInvocations;
		}

		List<TransitionActivation> outgoingTransitions = stateActivation.getOutgoingTransitions();
		for (TransitionActivation tActivation : outgoingTransitions) {
			Transition t = (Transition) tActivation.getNode();
			List<Trigger> triggers = t.getTriggers();
			for (Trigger trigger : triggers) {

				Event event = trigger.getEvent();

				if (event instanceof CallEvent) {

					

					validInvocations.add(new Invocation(objectActivation, tActivation, (CallEvent) event));
				}
			}
		}

		return validInvocations;
	}

}
