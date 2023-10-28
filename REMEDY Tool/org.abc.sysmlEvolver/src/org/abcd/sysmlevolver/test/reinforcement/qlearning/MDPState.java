package org.abcd.sysmlevolver.test.reinforcement.qlearning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.abcd.sysmlevolver.test.utils.FaultySenario;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateActivation;
import org.eclipse.uml2.uml.State;

public class MDPState {

	public Set<StateActivation> substates;
	public List<MDPTransition> incomingTransitions;
	public List<MDPTransition> outgoingTransitions;
	public int id;

	private boolean isFinalState = false;
	public MDPState shadowState = null;
	//
	public int stateVisitedNum;

	public MDPState(Set<StateActivation> states, int id) {
		substates = new HashSet<StateActivation>();
		substates.addAll(states);

		this.id = id;
		this.stateVisitedNum = 1;

		incomingTransitions = new ArrayList<MDPTransition>();
		outgoingTransitions = new ArrayList<MDPTransition>();
	}

	public boolean containsAll(List<State> states) {
		for (State state : states) {
			boolean matched = false;
			for (StateActivation stateActivation : substates) {
				if (stateActivation.getNode().equals(state)) {
					matched = true;
					break;
				}
			}
			if (!matched) {
				return false;
			}
		}
		return true;
	}

	public void removeFaultySenarios(List<FaultySenario> faultySenarios) {

		List<MDPTransition> transitionsToBeRemoved = new ArrayList<MDPTransition>();

		for (FaultySenario fs : faultySenarios) {
			if (this.containsAll(fs.substates)) {
				for (MDPTransition t : outgoingTransitions) {
					if (t.transitinoActivation.equals(fs.invocation.transitionActivation)) {
						transitionsToBeRemoved.add(t);
					}
				}
			}
		}

		if (transitionsToBeRemoved.isEmpty()) {
			return;
		}

		List<MDPTransition> newOutgoingTransitions = new ArrayList<MDPTransition>();
		for (MDPTransition t : outgoingTransitions) {
			if (!transitionsToBeRemoved.contains(t)) {
				newOutgoingTransitions.add(t);
			}
		}
		outgoingTransitions = newOutgoingTransitions;

	}

	public void addOutgoingTransition(MDPTransition transition) {

		if (transition.transitinoActivation == null) {
			assert (outgoingTransitions.size() == 1);
			for (MDPTransition outgoing : outgoingTransitions) {
				if (outgoing.transitinoActivation == null) {
					return;
				}
			}
		} else {
			for (MDPTransition outgoing : outgoingTransitions) {
				if (transition.transitinoActivation.equals(outgoing.transitinoActivation)) {
					return;
				}
			}
		}

		outgoingTransitions.add(transition);
	}

	public void addIncomingTransition(MDPTransition transition) {
		if (transition.transitinoActivation == null) {
			assert (incomingTransitions.size() == 1);
			for (MDPTransition incoming : incomingTransitions) {
				if (incoming.transitinoActivation == null) {
					return;
				}
			}
		} else {
			for (MDPTransition incoming : incomingTransitions) {
				if (transition.transitinoActivation.equals(incoming.transitinoActivation)) {
					return;
				}
			}
		}

		incomingTransitions.add(transition);
	}

	public void setFinalState() {
		isFinalState = true;
	}

	public double getQ(boolean useShadow) {

		if (useShadow) {
			return shadowState.getQ(false);
		}

		if (outgoingTransitions.isEmpty()) {
			if (isFinalState) {
				return 0;
			} else {
				return QLearningTester.initialReward; 
			}
		}

		double maxQ = 0;
		for (MDPTransition outgoingTransition : outgoingTransitions) {
			double q = outgoingTransition.getQ();
			if (q > maxQ) {
				maxQ = q;
			}
		}
		return maxQ;
	}

	public boolean equals(Set<StateActivation> activeSubstates) {

		if (this.substates.size() != activeSubstates.size()) {
			return false;
		}

		if (!this.substates.containsAll(activeSubstates)) {
			return false;
		} else {
			return true;
		}

	}

	public boolean isCovered() {

		if (shadowState != null || isFinalState) {
			return true;
		}

		for (MDPTransition transition : outgoingTransitions) {
			if ((!transition.isCovered()) && (!transition.getOperationName().equals("stop"))) {
				return false;
			}
		}
		return true;
	}

	public int getOutgoingTransitionCoverage() {

		int covered = 0;
		for (MDPTransition transition : outgoingTransitions) {
			if (transition.isCovered()) {
				covered++;
			}
		}
		return covered;
	}

	public String toString() {
		StringBuffer res = new StringBuffer();
		res.append("State");
		res.append(id);
		res.append(" (");
		for (StateActivation substate : substates) {
			if (substate.getNode() == null) {
				res.append("xxx ");
			} else {
				res.append(substate.getNode().getName());
			}
			res.append(", ");
		}
		res.append(") ");

		if (shadowState != null) {
			res.append("\n@ ");
			// System.err.println("shadowState.toString()-----" +
			// shadowState.toString());
			// res.append(shadowState.toString());
		}

		return res.toString();
	}

}
