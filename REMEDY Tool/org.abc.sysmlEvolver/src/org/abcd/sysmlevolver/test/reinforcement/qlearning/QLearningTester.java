package org.abcd.sysmlevolver.test.reinforcement.qlearning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.abcd.sysmlevolver.ExecutionParameters;
import org.abcd.sysmlevolver.model.Semantics.SysMLEvolverStateActivation;
import org.abcd.sysmlevolver.model.loci.ActiveObjectActivation;
import org.abcd.sysmlevolver.test.invocation.Invocation;
import org.abcd.sysmlevolver.test.log.PerformanceLogger;
import org.abcd.sysmlevolver.test.log.TestLogger;
import org.abcd.sysmlevolver.test.reinforcement.ReinforcementTester;
import org.abcd.sysmlevolver.test.sut.SUTObject;
import org.abcd.sysmlevolver.test.sut.SUTProxy;
import org.abcd.sysmlevolver.test.sut.SUTConnector.MsgType;
import org.abcd.sysmlevolver.test.utils.FaultySenario;
import org.abcd.sysmlevolver.test.utils.TestInputGenerator;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateConfiguration;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateMachineExecution;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.TransitionActivation;
import org.eclipse.uml2.uml.CallEvent;
import org.eclipse.uml2.uml.ChangeEvent;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Event;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Trigger;

public class QLearningTester extends ReinforcementTester {

	protected MarkovDecisionProcess mdp = new MarkovDecisionProcess();

	protected ArrayList<MDPTransition> lastTransitions = null;

	public static double learningRate = 0.1;
	public static double discountFactor = 0.98;
	public static double initialReward = 1;
	public static double epsilon = 0.8;
	public static double uFactor = 0.9;

	protected Random random = new Random();

	protected int epochIndex;

	@Override
	public boolean episodeStart(SUTObject sut, List<FaultySenario> faultySenarios) {
		lastTransitions = null;
		boolean res = mdp.updateReferences(sut.getActiveObjectActivations());
		epochIndex = 0;
		return res;
	}

	@Override
	public void episodeFinish(double fragility) {

	}

	@Override
	public Invocation nextEpoch(SUTObject sut, double reward) {
		epochIndex++;
		Map<String, ActiveObjectActivation> activeObjectActivations = sut.getActiveObjectActivations();

		if (lastTransitions == null) {
			Set<StateActivation> substates = getActiveStates(activeObjectActivations);
			mdp.initRoot(substates);
			mdp.updateAllQvalues();
		} else {
			Set<StateActivation> substates = getActiveStates(activeObjectActivations);
			mdp.correctCurrentState(substates, lastTransitions.get(lastTransitions.size() - 1));
		}

		for (ActiveObjectActivation objectActivation : activeObjectActivations.values()) {
			assert (objectActivation.classifierBehaviorInvocations.size() == 1);
			synchronized (objectActivation) {
				StateMachineExecution execution = (StateMachineExecution) (objectActivation.classifierBehaviorInvocations
						.get(0).execution);
				updateNextMDPStates(objectActivation, execution.getConfiguration().getRoot());
			}
		}


		if (!ExecutionParameters.usingSarsa) {
			if (lastTransitions != null && (!lastTransitions.isEmpty())) {
				MDPTransition lastTransition = lastTransitions.get(lastTransitions.size() - 1);
				if (lastTransition.invocation.isFaultInjection()) {
					mdp.setFinalState();
				}
				lastTransition.updateQ(reward);
				int index = lastTransitions.size() - 2;
				int end = lastTransitions.size() - 11;
				while (index >= 0 && index >= end) {
					lastTransitions.get(index).updateQ();
					index--;
				}
			}
		}

		long beginTime = System.nanoTime();
		MDPTransition bestTransition = explore();
		long stopTime = System.nanoTime();

		if (ExecutionParameters.logPerformance) {
			PerformanceLogger.logTraverseTrans(stopTime - beginTime);
		}


		if (bestTransition == null)
			return null;

		mdp.invoke(bestTransition);
		recordTransition(bestTransition);

		if (ExecutionParameters.usingSarsa) {
			if (lastTransitions != null && (!lastTransitions.isEmpty())) {
				MDPTransition lastTransition = lastTransitions.get(lastTransitions.size() - 1);
				if (lastTransition.invocation.isFaultInjection()) {
					mdp.setFinalState();
				}
				lastTransition.onPolicyUpdateQ(reward, bestTransition.getQ());
			}
		}

		return bestTransition.getInvocation();

	}

	protected boolean faultInjected() {
		if (lastTransitions != null && (!lastTransitions.isEmpty())) {
			MDPTransition lastTransition = lastTransitions.get(lastTransitions.size() - 1);
			if (lastTransition.invocation.isFaultInjection()) {
				return true;
			}
		}
		return false;
	}

	protected void updateNextMDPStates(ActiveObjectActivation objectActivation, StateConfiguration configuration) {
		for (StateConfiguration child : configuration.getChildren()) {
			updateNextMDPStates(objectActivation, child);
		}

		if (configuration.getVertexActivation() == null) {
			return;
		}

		for (TransitionActivation transitionActivation : configuration.getVertexActivation().getOutgoingTransitions()) {
			Transition transition = (Transition) transitionActivation.getNode();
			List<Trigger> triggers = transition.getTriggers();
			for (Trigger trigger : triggers) {
				Event event = trigger.getEvent();
				if (event instanceof CallEvent) {
					updateNextMDPStates(transitionActivation);

				} else if (event instanceof ChangeEvent) {
					updateNextMDPStates(transitionActivation);

					// }
				}
			}
		}
	}

	private void updateNextMDPStates(TransitionActivation transitionActivation) {

		Transition transition = (Transition) transitionActivation.getNode();
		Trigger trigger = transition.getTriggers().get(0);
		Event event = trigger.getEvent();

		if (event instanceof CallEvent) {

		} 
		else {
			return;
		}

		mdp.updateNextState(transitionActivation);

	}

	protected void recordTransition(MDPTransition selectedTransition) {
		if (lastTransitions == null) {
			lastTransitions = new ArrayList<MDPTransition>();
		}

		lastTransitions.add(selectedTransition);
	}

	protected MDPTransition explore() {
		if (lastTransitions != null && lastTransitions.size() > 0
				&& lastTransitions.get(lastTransitions.size() - 1).getOperationName().equals("stop")) {
			return null; 
		}

		MDPTransition bestTransition = null;
		List<MDPTransition> transitions = new ArrayList<MDPTransition>();
		for (MDPTransition transition : mdp.getCurrentState().outgoingTransitions) {
			if (transition.isNullTransition()) {
				continue;
			}
			if (epochIndex > maxEpochNum || this.faultInjected()) {
				if (transition.getOperationName().equals("stop")) {
					bestTransition = transition;
				}
			} else {
				if (epochIndex > 12) {
					if (transition.invocation.isFaultInjection()) {
						bestTransition = transition;
					}
				}
			}

			transitions.add(transition);

			if (transition != null) {
				transition.transitionVisitedNum++;
				mdp.getCurrentState().stateVisitedNum++;
			}
		}

		if (transitions.size() > 0) { 
			if (bestTransition == null) {

				if (!mdp.allTransitionCovered()) {
					bestTransition = selectHighestQvalue(transitions);
				} else {
					bestTransition = selectAccordingQvalue(transitions);

					bestTransition = selectAccordingTranvalue(transitions, mdp.getCurrentState());
					if (bestTransition == null) {

						for (MDPTransition transition : transitions) {
							if (transition.getOperationName().equals("mode")
									|| transition.getOperationName().equals("stop")) {
								bestTransition = transition;
							}
						}

					}
				}
			}
		}
		if (bestTransition == null) {
			return null;
		}

		mdp.invoke(bestTransition);
		recordTransition(bestTransition);


		return bestTransition;
	}

	public String sendAndReceiveMsg(MDPTransition bestTransition) {
		if (bestTransition == null)
			return null;
		Operation operation = bestTransition.getInvocation().operation;
		Constraint guard = bestTransition.getInvocation().guard;
		List<ParameterValue> lastTestInputs = null;
		if (guard == null) {
			lastTestInputs = new ArrayList<ParameterValue>();
		} else {
			lastTestInputs = TestInputGenerator.instance().generateTestInput(guard, operation.getOwnedParameters());
		}

		List<ParameterValue> copy = new ArrayList<ParameterValue>();
		for (ParameterValue pv : lastTestInputs) {
			copy.add(pv.copy());
		}
		String host = "192.168.1.104";
		String port = "20008";
		String cmd = copy.get(0).parameter.getName() + ":" + copy.get(0).values.get(0); // Format:
																						// thr:0.06
		boolean succ = SUTProxy.instance().send(host, port, cmd);
		if (!succ) {
			return null;
		}
		String res = SUTProxy.instance().receive(host, port, MsgType.MSG_TYPE_STATUS);
		if (res == null) {
			return null;
		}
		return res;
	}

	public String sendAndReceiveMsg(List<ParameterValue> lastTestInputs) {
		if (lastTestInputs == null || lastTestInputs.size() == 0)
			return null;

		List<ParameterValue> copy = new ArrayList<ParameterValue>();
		for (ParameterValue pv : lastTestInputs) {
			copy.add(pv.copy());
		}
		String host = "192.168.1.105";
		String port = "20008";

		String cmd1 = "recMsg#";
		for (ParameterValue pv : copy) {
			cmd1 += (pv.parameter.getName() + ":" + pv.values.get(0) + ";");
		}
		cmd1 = cmd1.substring(0, cmd1.length() - 1);

		boolean succ = SUTProxy.instance().send(host, port, cmd1);

		if (!succ) {
			return null;
		}
		String res = SUTProxy.instance().receive(host, port, MsgType.MSG_TYPE_STATUS);
		if (res == null) {
			return null;
		}
		return res;
	}

	public boolean areAllTransitionsCovered() {
		return mdp.allTransitionCovered();
	}

	protected MDPTransition selectHighestQvalue(List<MDPTransition> transitions) {

		double qvalue = -1;
		List<MDPTransition> bests = new ArrayList<MDPTransition>();
		for (MDPTransition transition : transitions) {
			if (transition.getQ() > qvalue) {
				qvalue = transition.getQ();
				bests = new ArrayList<MDPTransition>();
				bests.add(transition);
			} else if (transition.getQ() == qvalue) {
				bests.add(transition);
			}
		}
		return bests.get(random.nextInt(bests.size()));
	}



	private MDPTransition selectAccordingCoverage(List<MDPTransition> transitions) {

		if (transitions.size() == 1) {
			return transitions.get(0);
		}

		double minCoverage = -1;
		List<MDPTransition> leastCoveredTransitions = new ArrayList<MDPTransition>();
		for (MDPTransition t : transitions) {
			if (t.getOperationName().equals("stop")) {
				continue;
			}

			if (minCoverage == -1 || minCoverage > t.getCoverage()) {
				minCoverage = t.getCoverage();
				leastCoveredTransitions = new ArrayList<MDPTransition>();
				leastCoveredTransitions.add(t);
			} else if (t.getCoverage() == minCoverage) {
				leastCoveredTransitions.add(t);
			}
		}

		return leastCoveredTransitions.get(random.nextInt(leastCoveredTransitions.size()));
	}

	protected MDPTransition selectAccordingQvalue(List<MDPTransition> transitions) {
		if (transitions.size() == 1) {
			return transitions.get(0);
		}

		double highestQ = 0;
		List<MDPTransition> best = new ArrayList<MDPTransition>();

		for (MDPTransition t : transitions) {
			if (t.getQ() > highestQ) {
				highestQ = t.getQ();
				best = new ArrayList<MDPTransition>();
				best.add(t);
			} else if (t.getQ() == highestQ) {
				best.add(t);
			}
		}

		MDPTransition res = null;

		if (highestQ < 0.5) {
			res = selectAccordingCoverage(transitions);
		} else {
			if (random.nextFloat() < this.epsilon) {
				res = best.get(random.nextInt(best.size()));
			} else {
				res = transitions.get(random.nextInt(transitions.size()));
			}
		}

		return res;

		
	}

	protected MDPTransition selectAccordingTranvalue(List<MDPTransition> transitions, MDPState currentState) {
		if (transitions.size() == 1) {
			return transitions.get(0);
		}

		double highestQ = 0;
		List<MDPTransition> best = new ArrayList<MDPTransition>();
		double curValue = 0;
		double c = 0.99;
		int stateVisitedNum = currentState.stateVisitedNum;
		for (MDPTransition t : transitions) {
			curValue = t.getQ() + c * Math.sqrt(Math.log(stateVisitedNum / (t.transitionVisitedNum * 1.0)));
			if (curValue > highestQ) {
				highestQ = curValue;
				best = new ArrayList<MDPTransition>();
				best.add(t);
			} else if (curValue == highestQ) {
				best.add(t);
			}
		}

		MDPTransition res = null;
		if (random.nextFloat() < this.epsilon) {
			res = best.get(random.nextInt(best.size()));
		} else {
			res = transitions.get(random.nextInt(transitions.size()));
		}

		return res;

	}

	

	protected Set<StateActivation> getActiveStates(Map<String, ActiveObjectActivation> activeObjectActivations) {

		Set<StateActivation> substates = new HashSet<StateActivation>();

		for (ActiveObjectActivation objectActivation : activeObjectActivations.values()) {

			StateMachineExecution execution = (StateMachineExecution) (objectActivation.classifierBehaviorInvocations
					.get(0).execution);
			substates.addAll(getActiveStates(execution.getConfiguration().getRoot()));

		}

		return substates;
	}

	private List<SysMLEvolverStateActivation> getActiveStates(StateConfiguration configuration) {

		List<SysMLEvolverStateActivation> activeStates = new ArrayList<SysMLEvolverStateActivation>();
		for (StateConfiguration child : configuration.getChildren()) {
			activeStates.addAll(getActiveStates(child));
		}
		if (activeStates.size() > 0 || configuration.getVertexActivation() == null) {
			return activeStates;
		}

		SysMLEvolverStateActivation stateActivation = (SysMLEvolverStateActivation) configuration.getVertexActivation();
		activeStates.add(stateActivation);
		return activeStates;

	}

	public MarkovDecisionProcess getMDP() {
		return mdp;
	}

	public String toString() {

		StringBuffer str = new StringBuffer();

		str.append("state: \n");

		if (lastTransitions != null) {
			for (MDPTransition transition : lastTransitions) {

				str.append("S" + transition.source.id + ", ");
			}
		}
		str.append("\n");
		str.append(mdp.toString());
		return str.toString();
	}

	@Override
	public String toString(int nIteration) {
		StringBuffer str = new StringBuffer();

		str.append("state: \n");

		if (lastTransitions != null) {
			for (MDPTransition transition : lastTransitions) {

				str.append("S" + transition.source.id + ", ");
			}
		}
		str.append("\n");
		if (nIteration % 20 == 1) {
			str.append(mdp.toString());
		}

		return str.toString();
	}

	@Override
	public void logParameters() {

		StringBuffer str = new StringBuffer();
		str.append("epsilon: " + epsilon + "\n");
		str.append("learning rate: " + learningRate + "\n");
		str.append("discount factor: " + discountFactor + "\n");
		str.append("initial reward: " + initialReward + "\n");
		str.append("maxActionExplorationTimes: " + ExecutionParameters.maxActionExplorationTimes + "\n");
		str.append("usingSarsa: " + ExecutionParameters.usingSarsa);

		TestLogger.logParameters(str.toString());

	}

}
