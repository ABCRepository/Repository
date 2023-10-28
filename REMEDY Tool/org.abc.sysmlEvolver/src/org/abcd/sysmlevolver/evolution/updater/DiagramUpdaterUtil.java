package org.abcd.sysmlevolver.evolution.updater;

import org.abcd.sysmlevolver.evolution.updater.enotation.ENotationUtil;
import org.abcd.sysmlevolver.evolution.updater.euml.EState;
import org.abcd.sysmlevolver.evolution.updater.euml.ETransition;
import org.abcd.sysmlevolver.evolution.updater.euml.EUMLUtil;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Operation;

public class DiagramUpdaterUtil {

	public void evolutionTransitionOnCall(String curStateOwningRegionName, String curStateName, Operation bestOperation,
			Constraint guard, String targetStateName) {
		EUMLUtil umlUtil = new EUMLUtil();
		String file = "model";
		umlUtil.updateTransitionOnCall(file, curStateOwningRegionName, curStateName, bestOperation.getName(),
				guard.getSpecification().stringValue(), targetStateName);
		EState sourceState = umlUtil.sourceStateOnCall;
		EState targetState = umlUtil.targetStateOnCall;
		ETransition transition = umlUtil.evolveTransitionOnCall;
		ENotationUtil notationUtil = new ENotationUtil();
		String[] strs = file.split("/");
		String method = transition.getPid();
		String parentStateId = umlUtil.parentStateIdOnCall;
		String currentStateId = sourceState.getPid();
		String targetStateId = targetState.getPid();
		String stateMachineId = umlUtil.stateMachineIdOnCall;
		String classType = strs[strs.length - 1];
		notationUtil.updateTransitionNotationOnCall(file, method, parentStateId, currentStateId, targetStateId,
				stateMachineId, classType);
	}

	public void evolutionStateAndTransitionOnCall(String parentName, String currentStateName, String guardConstraint,
			String methodName, String newStargetStateName, String targetStateConstraint) {
		EUMLUtil umlUtil = new EUMLUtil();
		String file = "model";
		umlUtil.generateTransitionAndStateOnCall(file, parentName, currentStateName, methodName, guardConstraint,
				newStargetStateName, targetStateConstraint);
		EState sourceState = umlUtil.sourceStateOnCall;
		EState targetState = umlUtil.targetStateOnCall;
		ETransition transition = umlUtil.evolveTransitionOnCall;
		ENotationUtil notationUtil = new ENotationUtil();

		String[] strs = file.split("/");
		String method = transition.getPid();
		String parentStateId = umlUtil.parentStateIdOnCall;
		String currentStateId = sourceState.getPid();
		String targetStateId = targetState.getPid();
		String stateMachineId = umlUtil.stateMachineIdOnCall;
		String classType = strs[strs.length - 1];

		notationUtil.updateStateAndTransitionNotationOnCall(file, parentStateId, method, currentStateId, targetStateId,
				stateMachineId, classType);
	}

	public void evolutionStateAndTransitionOnChange(String curStateOwningRegionName, String currentStateName,
			String constraint, String changeExpressionName, String targetStateName, String initStateName,
			String idleStateName, String targetStateRegionName) {
		EUMLUtil umlUtil = new EUMLUtil();
		String file = "model";
		umlUtil.generateTransitionAndStateOnChange(file, curStateOwningRegionName, currentStateName, targetStateName,
				constraint, changeExpressionName, initStateName, idleStateName, targetStateRegionName);
		EState sourceState = umlUtil.sourceStateOnChange;
		EState targetState = umlUtil.targetStateOnChange;
		ETransition transition = umlUtil.evolveTransitionOnChange;
		ETransition dummytransition = umlUtil.dummyTransitionOnchange;
		EState initState = umlUtil.initialStateOnChange;
		EState idleState = umlUtil.idleStateOnChange;
		ETransition tranBetweenInitAndIdle = umlUtil.evolveTransitionOfInitAndIdleOnChange;

		ENotationUtil notationUtil = new ENotationUtil();

		String[] strs = file.split("/");
		String method = transition.getPid();
		String dummymethod = dummytransition.getPid();
		String parentStateId = umlUtil.parentStateIdOnChange;
		String currentStateId = sourceState.getPid();
		String targetStateId = targetState.getPid();
		String stateMachineId = umlUtil.stateMachineIdOnChange;
		String classType = strs[strs.length - 1];

		String regionId = umlUtil.regionIdOnChange;

		String initStateId = initState.getPid();
		String idleStateId = idleState.getPid();

		String transitionBetweenInitAndIdleStateId = tranBetweenInitAndIdle.getPid();

		notationUtil.updateStateAndTransitionNotationOnChange(file, parentStateId, method, dummymethod, currentStateId,
				targetStateId, stateMachineId, classType, regionId, initStateId, idleStateId,
				transitionBetweenInitAndIdleStateId);
	}

	public void evolutionTransitionOnChange(String parentRegionName, String currentStateName,
			String changeExpressionName, String targetStateName) {
		EUMLUtil umlUtil = new EUMLUtil();
		String file = "model";
		String parentName = "State Machine Region";
		umlUtil.generateTransitionOnChange(file, parentName, currentStateName, targetStateName, changeExpressionName);
		EState sourceState = umlUtil.sourceStateOnChange;
		EState targetState = umlUtil.targetStateOnChange;
		ETransition transition = umlUtil.evolveTransitionOnChange;
		ENotationUtil notationUtil = new ENotationUtil();
		String[] strs = file.split("/");
		String method = transition.getPid();
		String parentStateId = "State Machine Region";
		String currentStateId = sourceState.getPid();
		String targetStateId = targetState.getPid();
		String stateMachineId = umlUtil.stateMachineIdOnChange;
		String classType = strs[strs.length - 1];

		notationUtil.updateTransitionNotationOnChange(file, parentStateId, method, currentStateId, targetStateId,
				stateMachineId, classType);
	}

}
