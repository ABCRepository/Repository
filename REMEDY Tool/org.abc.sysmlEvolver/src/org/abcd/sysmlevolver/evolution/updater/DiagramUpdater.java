package org.abcd.sysmlevolver.evolution.updater;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.abc.constraint.ConstraintInstance;
import org.abc.constraint.ConstraintInstanceFactory;
import org.abcd.sysmlevolver.ExecutionParameters;
import org.abcd.sysmlevolver.evolution.updater.euml.RandomKeyUtil;
import org.abcd.sysmlevolver.model.Semantics.ActiveObject;
import org.abcd.sysmlevolver.model.Semantics.SysMLEvolverStateActivation;
import org.abcd.sysmlevolver.model.loci.ActiveObjectActivation;
import org.abcd.sysmlevolver.test.FeatureValueMonitor;
import org.abcd.sysmlevolver.test.log.PerformanceLogger;
import org.abcd.sysmlevolver.test.log.TestLogger;
import org.abcd.sysmlevolver.test.reinforcement.qlearning.MDPState;
import org.abcd.sysmlevolver.test.reinforcement.qlearning.MDPTransition;
import org.abcd.sysmlevolver.test.reinforcement.qlearning.QLearningTester;
import org.abcd.sysmlevolver.test.sut.SUTObject;
import org.abcd.sysmlevolver.test.utils.TestInputGenerator;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.ExtensionalValue;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.RegionActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateConfiguration;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateMachineExecution;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.TransitionActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.VertexActivation;
import org.eclipse.uml2.uml.ChangeEvent;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Event;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Trigger;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.ValueSpecification;


public class DiagramUpdater extends QLearningTester {
	protected SUTObject sut;
	protected List<Operation> operations;
	protected boolean isExistingOfChangeEvent;
	protected boolean isMatchActiveObjectActivationByChangeEvent;
	protected FeatureValueMonitor monitor;

	public DiagramUpdater() {
	}

	public DiagramUpdater(List<ExtensionalValue> extensionalValues, FeatureValueMonitor monitor) {
		this.monitor = monitor;
		operations = new ArrayList<Operation>();
		sut = new SUTObject();
		sut.init(extensionalValues);
		isExistingOfChangeEvent = false;
		isMatchActiveObjectActivationByChangeEvent = false;
		for (ExtensionalValue value : extensionalValues) {
			if (value instanceof ActiveObject) {
				addOperations((ActiveObject) value);
			}
		}
	}

	public synchronized void init(List<ExtensionalValue> extensionalValues) {
		operations.clear();
		for (ExtensionalValue value : extensionalValues) {
			if (value instanceof ActiveObject) {
				addOperations((ActiveObject) value);
			}
		}
	}

	

	public StateConfiguration getCurrentStateConfiguration(MDPState curState) {
		Map<String, ActiveObjectActivation> activeObjectctivations = sut.getActiveObjectActivations();
		Set<StateConfiguration> substates = getActiveStateConfigurations(activeObjectctivations);
		Set<StateActivation> stateActivationSet = curState.substates;
		for (StateConfiguration configuration : substates) {
			for (StateActivation activation : stateActivationSet) {
				if (((StateActivation) configuration.getVertexActivation()).equals(activation)) {
					return configuration;
				}
			}
		}
		return null;
	}

	public String getCurrentStateLevel(MDPState curState) {
		Map<String, ActiveObjectActivation> activeObjectctivations = sut.getActiveObjectActivations();
		Set<StateConfiguration> substates = getActiveStateConfigurations(activeObjectctivations);
		Set<StateActivation> stateActivationSet = curState.substates;
		for (StateConfiguration configuration : substates) {
			for (StateActivation activation : stateActivationSet) {
				if (((StateActivation) configuration.getVertexActivation()).equals(activation)) {
					return "L2";
				}
			}
		}
		return "L1";
	}

	public VertexActivation getCurrentState(MDPState curState) {
		Map<String, ActiveObjectActivation> activeObjectctivations = sut.getActiveObjectActivations();
		Set<StateConfiguration> substates = getActiveStateConfigurations(activeObjectctivations);
		Set<StateActivation> stateActivationSet = curState.substates;
		for (StateConfiguration configuration : substates) {
			for (StateActivation activation : stateActivationSet) {
				if (((StateActivation) configuration.getVertexActivation()).equals(activation)) {
					return configuration.getVertexActivation();
				}
			}
		}
		return null;
	}

	private Set<StateConfiguration> getActiveStateConfigurations(
			Map<String, ActiveObjectActivation> activeObjectctivations) {
		Set<StateConfiguration> substates = new HashSet<StateConfiguration>();
		for (ActiveObjectActivation objectActivation : activeObjectctivations.values()) {
			StateMachineExecution execution = (StateMachineExecution) (objectActivation.classifierBehaviorInvocations
					.get(0).execution);
			substates.addAll(getActiveStates(execution.getConfiguration().getRoot()));
		}
		return substates;
	}

	private List<StateConfiguration> getActiveStates(StateConfiguration configuration) {
		List<StateConfiguration> activeStates = new ArrayList<StateConfiguration>();
		for (StateConfiguration child : configuration.getChildren()) {
			activeStates.addAll(getActiveStates(child));
		}
		if (activeStates.size() > 0 || configuration.getVertexActivation() == null) {
			return activeStates;
		}
		activeStates.add(configuration);
		return activeStates;
	}

	
	protected void addOperations(ActiveObject object) {
		for (Class type : object.types) {
			for (Operation operation : type.getOperations()) {
					operations.add(operation);
			}
		}
	}

	@Override
	protected MDPTransition explore() {

		if (lastTransitions != null && lastTransitions.size() > 0
				&& lastTransitions.get(lastTransitions.size() - 1).getOperationName().equals("stop")) {
			return null; 
		}
		MDPTransition bestTransition = null;
		if (epochIndex == 1) {
			List<MDPTransition> transitions = new ArrayList<MDPTransition>();
			for (MDPTransition transition : mdp.getCurrentState().outgoingTransitions) {
				if (transition.isNullTransition()) {
					continue;
				}
				if (epochIndex > maxEpochNum || this.faultInjected()) {
				} //
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
		} 
		else {
			Operation bestOperation = selectOperationByReward(operations, mdp.getCurrentState().outgoingTransitions);
			for (MDPTransition transition : mdp.getCurrentState().outgoingTransitions) {
				if (transition.isNullTransition()) {
					continue;
				}
				if (transition.getOperationName().equals(bestOperation.getName())) {
					bestTransition = transition;
				}
				if (bestTransition != null) {
					transition.transitionVisitedNum++;
					mdp.getCurrentState().stateVisitedNum++;
					break;
				}
			}
			if (bestTransition == null) {
				MDPState curState = mdp.getCurrentState();
				String level = getCurrentStateLevel(curState);
				StateConfiguration currentStateconfiguration = getCurrentStateConfiguration(curState);
				if (level.equals("L2")) {
					String guardString = "";
					for (Parameter par : bestOperation.getOwnedParameters()) {
						String name = par.getName();
						guardString += name + " > 0 and " + name + " < 1";
						guardString += " and ";
					}
					guardString = guardString.substring(0, guardString.length() - 5);
					ValueSpecification specification = stringToValueSpecification(guardString);


					Constraint guard = valueSpecificationToConstraint(specification);
					List<ParameterValue> lastTestInputs = TestInputGenerator.instance().generateTestInput(guard,
							bestOperation.getOwnedParameters());
					String msg = sendAndReceiveMsg(lastTestInputs);

					VertexActivation stateActivation = currentStateconfiguration.getVertexActivation();
					String curStateName = stateActivation.getNode().getName();
					VertexActivation parentStateActivation = stateActivation.getParentVertexActivation();
					RegionActivation curStateOwningRegion = stateActivation.getOwningRegionActivation();
					String curStateOwningRegionName = curStateOwningRegion.getNode().getName();
					List<RegionActivation> childRegions = ((StateActivation) parentStateActivation)
							.getRegionActivation();

					boolean flag = false;
					String targetStateName = null;

					Constraint parentInvariant = ((State) parentStateActivation.getNode()).getStateInvariant();
					if (parentInvariant == null)
						flag = false;
					ConstraintInstance constraintInstance = ConstraintInstanceFactory.instance()
							.createConstraintInstanceByReceiveMsg(parentInvariant.getSpecification().stringValue(),
									msg);
					flag = constraintInstance.evaluate();

					if (flag == true) { 

						boolean isNewTranOnCall = false;
						for (RegionActivation region : childRegions) {
							for (VertexActivation child : region.getVertexActivations()) {
								if (child instanceof SysMLEvolverStateActivation) {
									Constraint invariant = ((State) child.getNode()).getStateInvariant();
									if (invariant != null) {
										ConstraintInstance constraint = ConstraintInstanceFactory.instance()
												.createConstraintInstanceByReceiveMsg(
														invariant.getSpecification().stringValue(), msg);

										isNewTranOnCall = constraint.evaluate();
										if (isNewTranOnCall == true) {
											targetStateName = child.getNode().getName();
											break;
										} //
										else {
											flag = false;
										}
									}
								}
							}
						}
						if (isNewTranOnCall == true) {


							ExecutionParameters.numsOfEvolvedTransitionofCall++;
							long beginTime = System.nanoTime();

							



							long stopTime = System.nanoTime();

							if (ExecutionParameters.logPerformance) {
								PerformanceLogger.logEvolveStatesAndTrans(stopTime - beginTime);
							}

							for (MDPTransition tran : mdp.getCurrentState().outgoingTransitions) {
								if (tran.getOperationName().equals(bestOperation.getName())) {
									bestTransition = tran;
								}
							}
						} //
						else {


							ExecutionParameters.numsOfEvolvedTransitionofCall++;
							ExecutionParameters.numsOfEvolvedStatesofCall++;
							long beginTime = System.nanoTime();

							String guardconstraint = guard.getSpecification().stringValue();
							String methodName = bestOperation.getName();
							String newTargetStateName = null;
							String targetStateConstraint = null;// "isObstacle =
																// false and
							if (methodName.equals("throttle")) {
								newTargetStateName = "Accelerating " + RandomKeyUtil.getPid();
								targetStateConstraint = "thr > 0 and thr < 1 and right = 0 and left = 0";
							} //
							else if (methodName.equals("brake")) {
								newTargetStateName = "Decelerating " + RandomKeyUtil.getPid();
								targetStateConstraint = "brake > 0 and brake < 1 and right = 0 and left = 0";
							} //
							else if (methodName.equals("turnLeft")) {
								newTargetStateName = "TurnLeft " + RandomKeyUtil.getPid();
								targetStateConstraint = "left > 0 and left < 1 and thr > 0 and thr < 1";
							} //
							else if (methodName.equals("turnRight")) {
								newTargetStateName = "TurnRight " + RandomKeyUtil.getPid();
								targetStateConstraint = "right > 0 and right < 1 and thr > 0 and thr < 1";
							} //
							else if (methodName.equals("turnLeftBrake")) {
								newTargetStateName = "TurnLeftBrake " + RandomKeyUtil.getPid();
								targetStateConstraint = "left > 0 and left < 1 and brake > 0 and brake < 1";
							} //
							else if (methodName.equals("turnRightBrake")) {
								newTargetStateName = "TurnRightBrake " + RandomKeyUtil.getPid();
								targetStateConstraint = "right > 0 and right < 1 and brake > 0 and brake < 1";
							} //

							


							long stopTime = System.nanoTime();

							if (ExecutionParameters.logPerformance) {
								PerformanceLogger.logEvolveStatesAndTrans(stopTime - beginTime);
							}

							for (MDPTransition tran : mdp.getCurrentState().outgoingTransitions) {
								if (tran.getOperationName().equals(bestOperation.getName())) {
									bestTransition = tran;
								}
							}
						}
					}
					if (flag == false) {
						boolean isNewState = false;
						boolean isContains = false;
						RegionActivation topRegion = parentStateActivation.getOwningRegionActivation();
						String currentStateName = parentStateActivation.getNode().getName();
						for (VertexActivation child : topRegion.getVertexActivations()) {
							if (child instanceof SysMLEvolverStateActivation) {
								Constraint invariant = ((State) child.getNode()).getStateInvariant();
								if (invariant != null) {
									ConstraintInstance constraint = ConstraintInstanceFactory.instance()
											.createConstraintInstanceByReceiveMsg(
													invariant.getSpecification().stringValue(), msg);
									isContains = constraint.evaluate();
									if (isContains == true) {
										targetStateName = child.getNode().getName();
										isNewState = false;
										break;
									} //
									else {
										isNewState = true;
									}
								}
							}
						} //
						if (isContains == true) {

							long beginTime = System.nanoTime();// ns

							
							String topRegionName = topRegion.getNode().getName();
							
							

							isExistingOfChangeEvent = false;
							

							if (!isExistingOfChangeEvent) {

								
								

								ActiveObjectActivation currentActivation = getActiveObjectActivation(
										changeExpressionName);
								if (currentActivation != null) {
									
									monitor.registerChangeEvent(currentActivation);
								}

							}

							long stopTime = System.nanoTime();

							if (ExecutionParameters.logPerformance) {
								PerformanceLogger.logEvolveStatesAndTrans(stopTime - beginTime);
							}
						} //
						if (isNewState == true) {
							long beginTime = System.nanoTime();

							
							String constraint = "";
							String changeExpressionName = constraint;
							
							String topRegionName = topRegion.getNode().getName();
							targetStateName = topRegionName + "" + RandomKeyUtil.getPid();
							String initStateName = "initial " + RandomKeyUtil.getPid();
							String idleStateName = "idle " + RandomKeyUtil.getPid();
							String targetStateRegionName = "Region " + RandomKeyUtil.getPid();


							


							long stopTime = System.nanoTime();

							if (ExecutionParameters.logPerformance) {
								PerformanceLogger.logEvolveStatesAndTrans(stopTime - beginTime);
							}

							ActiveObjectActivation currentActivation = getActiveObjectActivation(changeExpressionName);
							if (currentActivation != null) {
								monitor.registerChangeEvent(currentActivation);
							}

						} //
					}

				}
			}
		}
		if (bestTransition != null) {
			bestTransition.transitionVisitedNum++;
			mdp.getCurrentState().stateVisitedNum++;
		}
		return bestTransition;
	}

	public void matchExistingChangeBasedOnRoot(String changeExpressionName) {
		Map<String, ActiveObjectActivation> activeObjectctivations = sut.getActiveObjectActivations();
		for (ActiveObjectActivation objectActivation : activeObjectctivations.values()) {
			StateMachineExecution execution = (StateMachineExecution) (objectActivation.classifierBehaviorInvocations
					.get(0).execution);
			matchExistingChangeEvent(execution.getConfiguration().getRoot(), changeExpressionName);
		}
	}

	public void matchExistingChangeEvent(StateConfiguration configuratuion, String changeExpressionName) {
		for (StateConfiguration child : configuratuion.getChildren()) {
			matchExistingChangeEvent(child, changeExpressionName);
		}
		VertexActivation vertexActivation = configuratuion.getVertexActivation();
		if (vertexActivation == null)
			return;
		for (TransitionActivation transition : vertexActivation.getOutgoingTransitions()) {
			List<Trigger> triggers = ((Transition) transition.getNode()).getTriggers();
			for (Trigger trigger : triggers) {
				Event event = trigger.getEvent();
				if (event instanceof ChangeEvent) {
					String eventName = ((ChangeEvent) event).getChangeExpression().stringValue();
					if (eventName.equals(changeExpressionName) || eventName.contains(changeExpressionName)
							|| changeExpressionName.contains(eventName)) {
						isExistingOfChangeEvent = true;

						return;
					}
				}
			}
		}
	}

	public ActiveObjectActivation getActiveObjectActivation(String changeExpressionName) {
		Map<String, ActiveObjectActivation> activeObjectctivations = sut.getActiveObjectActivations();
		for (ActiveObjectActivation objectActivation : activeObjectctivations.values()) {
			StateMachineExecution execution = (StateMachineExecution) (objectActivation.classifierBehaviorInvocations
					.get(0).execution);
			matchActiveObjectActivation(execution.getConfiguration().getRoot(), changeExpressionName);
			if (isMatchActiveObjectActivationByChangeEvent == true)
				return objectActivation;
		}
		return null;
	}

	public void matchActiveObjectActivation(StateConfiguration configuratuion, String changeExpressionName) {
		for (StateConfiguration child : configuratuion.getChildren()) {
			matchActiveObjectActivation(child, changeExpressionName);
		}
		VertexActivation vertexActivation = configuratuion.getVertexActivation();
		if (vertexActivation == null)
			return;
		for (TransitionActivation transition : vertexActivation.getOutgoingTransitions()) {
			List<Trigger> triggers = ((Transition) transition.getNode()).getTriggers();
			for (Trigger trigger : triggers) {
				Event event = trigger.getEvent();
				if (event instanceof ChangeEvent) {
					String eventName = ((ChangeEvent) event).getChangeExpression().stringValue();
					if (eventName.equals(changeExpressionName)) {
						isMatchActiveObjectActivationByChangeEvent = true;
						return;
					}
				}
			}
		}
		return;
	}

	private Operation selectOperationByRandom(List<Operation> operations) {// to
																			// do
		List<Operation> newOperations = new ArrayList<Operation>();
		for (Operation operation : operations) {
			
			newOperations.add(operation);
		}
		return newOperations.get(random.nextInt(newOperations.size()));
	}


	private Operation selectOperationByReward(List<Operation> operations, List<MDPTransition> transitions) {
		double[] rewardByOperations = new double[operations.size()];
		double maxValue = -1;
		for (int i = 0; i < operations.size(); i++) {
			boolean used = false;
			double reward = 0;
			for (MDPTransition transition : transitions) {
				Operation tempOperation = operations.get(i);
				if (tempOperation.getName().equals(transition.invocation.operation.getName())) {
					used = true;
					reward = transition.getQ();
					continue;
				}
			}
			if (used == true) {
				rewardByOperations[i] = reward;
			} //
			else {
				rewardByOperations[i] = 1;
			}
		}

		for (int i = 0; i < rewardByOperations.length; i++) {
			if (rewardByOperations[i] > maxValue) {
				maxValue = rewardByOperations[i];
			}
		}

		List<Integer> indexList = new ArrayList<Integer>();
		for (int i = 0; i < rewardByOperations.length; i++) {
			if (rewardByOperations[i] == maxValue) {
				indexList.add(i);
			}
		}
		return operations.get(indexList.get(random.nextInt(indexList.size())));
	}

	public ValueSpecification stringToValueSpecification(String string) {
		LiteralString literal = UMLFactory.eINSTANCE.createLiteralString();
		literal.setValue(string);

		return literal;
	}

	public Constraint valueSpecificationToConstraint(ValueSpecification literal) {
		Constraint constraint = UMLFactory.eINSTANCE.createConstraint();
		constraint.setSpecification(literal);
		return constraint;
	}

	

	
}
