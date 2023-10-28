package org.abcd.sysmlevolver.test.reinforcement.random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.abc.constraint.ConstraintInstance;
import org.abc.constraint.ConstraintInstanceFactory;
import org.abcd.sysmlevolver.ExecutionParameters;
import org.abcd.sysmlevolver.evolution.updater.DiagramUpdater;
import org.abcd.sysmlevolver.evolution.updater.DiagramUpdaterUtil;
import org.abcd.sysmlevolver.evolution.updater.euml.RandomKeyUtil;
import org.abcd.sysmlevolver.model.Semantics.ActiveObject;
import org.abcd.sysmlevolver.model.Semantics.SysMLEvolverStateActivation;
import org.abcd.sysmlevolver.test.FeatureValueMonitor;
import org.abcd.sysmlevolver.test.log.PerformanceLogger;
import org.abcd.sysmlevolver.test.log.TestLogger;
import org.abcd.sysmlevolver.test.reinforcement.qlearning.MDPState;
import org.abcd.sysmlevolver.test.reinforcement.qlearning.MDPTransition;
import org.abcd.sysmlevolver.test.sut.SUTObject;
import org.abcd.sysmlevolver.test.utils.TestInputGenerator;
import org.eclipse.emf.common.util.EList;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.ExtensionalValue;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.RegionActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateConfiguration;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.VertexActivation;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.ValueSpecification;

public class DiagramUpdaterByRandomTester extends DiagramUpdater {

	public DiagramUpdaterByRandomTester(List<ExtensionalValue> extensionalValues, FeatureValueMonitor monitor) {
		this.monitor = monitor;
		operations = new ArrayList<Operation>();
		isExistingOfChangeEvent = false;
		isMatchActiveObjectActivationByChangeEvent = false;
		sut = new SUTObject();
		sut.init(extensionalValues);
		for (ExtensionalValue value : extensionalValues) {
			if (value instanceof ActiveObject) {
				addOperations((ActiveObject) value);
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
						bestTransition = transition;
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
									bestTransition = transition;
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
			Operation bestOperation = selectOperationByRandom(operations);
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
				StateConfiguration currentStateconfiguration = getCurrentStateConfiguration(curState);
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


							long beginTime = System.nanoTime();

							DiagramUpdaterUtil diagramUpdaterUtil = new DiagramUpdaterUtil();
							diagramUpdaterUtil.evolutionTransitionOnCall(curStateOwningRegionName, curStateName,
									bestOperation, guard, targetStateName);


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


							long beginTime = System.nanoTime();

							String guardconstraint = guard.getSpecification().stringValue();
							String methodName = bestOperation.getName();

							String newTargetStateName = null;
							String targetStateConstraint = null;
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

							DiagramUpdaterUtil diagramUpdaterUtil = new DiagramUpdaterUtil();


							diagramUpdaterUtil.evolutionStateAndTransitionOnCall(curStateOwningRegionName, curStateName,
									guardconstraint, methodName, newTargetStateName, targetStateConstraint);



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

							DiagramUpdater updater = new DiagramUpdater();
							String changeExpressionName = updater.StringToConstraint(msg);

							isExistingOfChangeEvent = false;
							matchExistingChangeBasedOnRoot(changeExpressionName);

							long stopTime = System.nanoTime();

							if (ExecutionParameters.logPerformance) {
								PerformanceLogger.logEvolveStatesAndTrans(stopTime - beginTime);
							}
						} //
						if (isNewState == true) {

							long beginTime = System.nanoTime();

							DiagramUpdater updater = new DiagramUpdater();
							String constraint = updater.StringToConstraint(msg);
							String changeExpressionName = constraint;
							DiagramUpdaterUtil diagramUpdaterUtil = new DiagramUpdaterUtil();
							String topRegionName = topRegion.getNode().getName();
							targetStateName = topRegionName + "" + RandomKeyUtil.getPid();
							String initStateName = RandomKeyUtil.getPid();
							String idleStateName = RandomKeyUtil.getPid();
							String targetStateRegionName = RandomKeyUtil.getPid();

							diagramUpdaterUtil.evolutionStateAndTransitionOnChange(topRegionName, currentStateName,
									constraint, changeExpressionName, targetStateName, initStateName, idleStateName,
									targetStateRegionName);


							long stopTime = System.nanoTime();

							if (ExecutionParameters.logPerformance) {
								PerformanceLogger.logEvolveStatesAndTrans(stopTime - beginTime);
							}

						} //
					}

			}
		}
		if (bestTransition != null) {
			bestTransition.transitionVisitedNum++;
			mdp.getCurrentState().stateVisitedNum++;

		}

		return bestTransition;
	}

	private Operation selectOperationByRandom(List<Operation> operations) {// to
		List<Operation> newOperations = new ArrayList<Operation>();
		for (Operation operation : operations) {
			newOperations.add(operation);
		}
		return newOperations.get(random.nextInt(newOperations.size()));
	}

}
