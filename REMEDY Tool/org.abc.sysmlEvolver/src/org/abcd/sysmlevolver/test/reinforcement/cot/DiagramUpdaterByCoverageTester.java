package org.abcd.sysmlevolver.test.reinforcement.cot;

import java.util.ArrayList;
import java.util.List;

import org.abc.constraint.ConstraintInstance;
import org.abc.constraint.ConstraintInstanceFactory;
import org.abcd.sysmlevolver.ExecutionParameters;
import org.abcd.sysmlevolver.evolution.updater.DiagramUpdater;
import org.abcd.sysmlevolver.evolution.updater.euml.RandomKeyUtil;
import org.abcd.sysmlevolver.model.Semantics.ActiveObject;
import org.abcd.sysmlevolver.model.Semantics.SysMLEvolverStateActivation;
import org.abcd.sysmlevolver.model.loci.ActiveObjectActivation;
import org.abcd.sysmlevolver.test.FeatureValueMonitor;
import org.abcd.sysmlevolver.test.log.PerformanceLogger;
import org.abcd.sysmlevolver.test.log.TestLogger;
import org.abcd.sysmlevolver.test.reinforcement.qlearning.MDPState;
import org.abcd.sysmlevolver.test.reinforcement.qlearning.MDPTransition;
import org.abcd.sysmlevolver.test.sut.SUTObject;
import org.abcd.sysmlevolver.test.utils.TestInputGenerator;
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

public class DiagramUpdaterByCoverageTester extends DiagramUpdater {

	public DiagramUpdaterByCoverageTester(List<ExtensionalValue> extensionalValues, FeatureValueMonitor monitor) {
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
					if (transition.getOperationName().equals("stop")) {
						bestTransition = transition;
					}
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
			Operation bestOperation = selectOperationByCoverage(operations, mdp.getCurrentState().outgoingTransitions);
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
					guardString = guardString.substring(0, guardString.length() - 5);
					ValueSpecification specification = stringToValueSpecification(guardString);

					Constraint guard = valueSpecificationToConstraint(specification);
					List<ParameterValue> lastTestInputs = TestInputGenerator.instance().generateTestInput(guard,
							bestOperation.getOwnedParameters());
					String msg = "";

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
							
							String changeExpressionName = msg;

							isExistingOfChangeEvent = false;
							matchExistingChangeBasedOnRoot(changeExpressionName);


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

							
							String constraint = msg;
							String changeExpressionName = constraint;
							
							String topRegionName = topRegion.getNode().getName();
							targetStateName = topRegionName + "" + RandomKeyUtil.getPid();
							String initStateName = RandomKeyUtil.getPid();
							String idleStateName = RandomKeyUtil.getPid();
							String targetStateRegionName = RandomKeyUtil.getPid();


							


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

					if (msg == null) {
						return null;
					} //
				
			}
		}
		if (bestTransition != null) {
			bestTransition.transitionVisitedNum++;
			mdp.getCurrentState().stateVisitedNum++;
		}
		return bestTransition;
	}

	private Operation selectOperationByCoverage(List<Operation> operations, List<MDPTransition> transitions) {
		double[] coverageByOperations = new double[operations.size()];
		double minCoverage = 99;

		for (int i = 0; i < operations.size(); i++) {
			boolean used = false;
			double coverage = 0;
			for (MDPTransition transition : transitions) {
				Operation tempOperation = operations.get(i);
				if (tempOperation.getName().equals(transition.invocation.operation.getName())) {
					used = true;
					coverage = transition.getCoverage();
					continue;
				}
			}
			if (used == true) {
				coverageByOperations[i] = coverage;
			} //
			else {
				coverageByOperations[i] = 0;
			}
		}

		for (int i = 0; i < coverageByOperations.length; i++) {
			if (coverageByOperations[i] < minCoverage) {
				minCoverage = coverageByOperations[i];
			}
		}

		List<Integer> indexList = new ArrayList<Integer>();
		for (int i = 0; i < coverageByOperations.length; i++) {
			if (coverageByOperations[i] == minCoverage) {
				indexList.add(i);
			}
		}

		return operations.get(indexList.get(random.nextInt(indexList.size())));
	}
}
