package org.abcd.sysmlevolver.model.loci;

import java.util.ArrayList;
import java.util.List;

import org.abcd.sysmlevolver.ExecutionParameters;
import org.abcd.sysmlevolver.model.Semantics.ActiveObject;
import org.abcd.sysmlevolver.test.log.PerformanceLogger;
import org.eclipse.papyrus.moka.async.fuml.debug.AsyncControlDelegate;
import org.eclipse.papyrus.moka.async.fuml.debug.AsyncDebug;
import org.eclipse.papyrus.moka.fuml.FUMLExecutionEngine;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.CompleteActions.AcceptEventActionEventAccepter;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventAccepter;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.InvocationEventOccurrence;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.SignalEventOccurrence;
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.ChoiceStrategy;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.CommonBehavior.CallEventOccurrence;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.CommonBehavior.ChangeEventOccurrence;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.CommonBehavior.SM_ObjectActivation;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Operation;

public class ActiveObjectActivation extends SM_ObjectActivation {

	private boolean isTraversing = false; 

	private String operationBeingInvoked = null;

	public ActiveObjectActivation(Class classifier, List<ParameterValue> inputs) {
		super(classifier, inputs);
	}

	public void setTraversing(boolean isTraversing) {
		this.isTraversing = isTraversing;
	}

	public boolean operationBeingInvoked() {
		return operationBeingInvoked != null;
	}

	public boolean isTraversing() {
		return isTraversing;
	}

	public void send(EventOccurrence eventOccurrence) {
		this.evtPool.send(eventOccurrence);
	}

	public void callOperation(Operation operation, List<ParameterValue> parameterValues) {
		long beginTime = System.nanoTime();

		operationBeingInvoked = operation.getName();
		((ActiveObject) object).callOperation(operation.getName(), parameterValues);

		long stopTime = System.nanoTime();

		if (ExecutionParameters.logPerformance) {
			PerformanceLogger.logExecuteOperation(stopTime - beginTime);
		}

	}

	@Override
	public EventOccurrence getNextEvent() {
		// Added for connection with debug API
		if (this.evtPool.isEmpty()) {
			// System.err.println("evt Pool-------empty--");
			this.currentState = ObjectActivationState.WAITING;
			this.hasBeenWaiting = true;
			((AsyncControlDelegate) FUMLExecutionEngine.eInstance.getControlDelegate()).notifyWaitingStateEntered(this);
		}
		
		EventOccurrence eventOccurrence = evtPool.getNextEvent();
		
		this.currentState = ObjectActivationState.RUNNING;

		return eventOccurrence;
	}

	
	}

	public String getName() {
		return this.classifier.getName();
	}

}
