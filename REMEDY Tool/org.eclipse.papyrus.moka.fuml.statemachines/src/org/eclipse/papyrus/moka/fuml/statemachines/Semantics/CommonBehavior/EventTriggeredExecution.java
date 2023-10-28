/*****************************************************************************
 * Copyright (c) 2016 CEA LIST.
 *
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Jeremie Tatibouet (CEA LIST)
 *
 *****************************************************************************/
package org.eclipse.papyrus.moka.fuml.statemachines.Semantics.CommonBehavior;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.Execution;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.SignalEventOccurrence;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Parameter;

public class EventTriggeredExecution extends Execution {

	// Original execution
	public Execution wrappedExecution;
	
	// Event occurrence whose dispatching implied the
	// the execution of the behavior
	public EventOccurrence triggeringEventOccurrence;
	
	protected void initialize(){
		// Transfer input parameter values of the call event execution
		// to the wrapped execution if possible. Two situations are considered
		// 1. If the triggering EventOccurrence is for a SignalEvent, then all
		//    executed behavior will have either one parameter or no parameters.
		//    If a behavior has a Parameter, the SignalInstance corresponding to
		//    the SignalEventOccurrence is passed into the behavior Execution as
		//    the value of its parameter.
		// 2. If the triggering EventOccurrence is for a CallEvent, then all executed
		//    behaviors will have either no Parameters or signatures that conform or
		//    input conform to the operation being called.
		//    If a Behavior has Parameters, then the values of the input Parameters
		//    of for the call are passed into the Behavior Execution as the values
		//    of the corresponding input Parameters of the Behavior.
		this._beginIsolation();
		if(this.wrappedExecution.getBehavior().getOwnedParameters().size() > 0){
			Behavior behavior = this.wrappedExecution.getBehavior();
			if(this.triggeringEventOccurrence instanceof SignalEventOccurrence){
				SignalEventOccurrence signalEventOccurrence = (SignalEventOccurrence) this.triggeringEventOccurrence;
				if(behavior.inputParameters().size() == 1){
					Parameter parameter = behavior.inputParameters().get(0);
					ParameterValue parameterValue = new ParameterValue();
					parameterValue.parameter = parameter;
					List<Value> values = new ArrayList<Value>();
					values.add(signalEventOccurrence.signalInstance);
					parameterValue.values = values;
					this.wrappedExecution.setParameterValue(parameterValue);
				}
			}else if(this.triggeringEventOccurrence instanceof CallEventOccurrence){
				CallEventOccurrence callEventOccurrence = (CallEventOccurrence) this.triggeringEventOccurrence;
				List<Parameter> behaviorInputParameters = behavior.inputParameters();
				List<ParameterValue> inputParameterValues = callEventOccurrence.execution.getInputParameterValues();
				if(behaviorInputParameters.size() == inputParameterValues.size()){
					int i = 1;
					while(i <= behaviorInputParameters.size()){
						ParameterValue parameterValue = new ParameterValue();
						parameterValue.parameter = behaviorInputParameters.get(i - 1);
						parameterValue.values = inputParameterValues.get(i - 1).values;
						this.wrappedExecution.setParameterValue(parameterValue);
						i++;
					}
				}
			}
		}
		this._endIsolation();
	}
	
	@Override
	public void execute() {
		// First the behavior handled by the wrapped execution is parameterized
		// with parameter input values provided by the triggering event occurrence.
		// The behavior handled by the wrapped Execution is executed and finally outputs
		// are passed out to the triggering event occurrence (only occurs in the case of
		// a call event occurrence).
		if(this.wrappedExecution != null && this.triggeringEventOccurrence != null){
			this.initialize();
			this.wrappedExecution.execute();
			this.finalize();
		}
	}
	
	protected void finalize(){
		// Transfer output parameter values (produced by the wrapped execution) back to
		// the execution associated t the call event.
		// If an effect, entry or exit Behavior is not just input-conforming, then the
		// values of its output Parameters are passed out of its Behavior Execution on
		// its completion as potential values for the output Parameters of the called 
		// Operation.
		// 
		// Notes: 
		//    If the CallEvent is for a synchronous call, then the call ends at the end
		//    of the triggered run-to-completion (RTC) step. If the called Operation has
		//    output Parameters, then the values returned for those parameters are those
		//    produced by the last effect, entry or exit Behavior to complete its execution
		//    during the RTC step. Since some or all of those Behaviors may execute concurrently,
		//    which one completes “last” may be only partially determined by the specified semantics.
		//    The values returned may legally be those produced any Behavior that produces potential
		//    output values and is the last to complete in any execution trace for the RTC
		//    step consistent with the specified StateMachine semantics.
		this._beginIsolation();
		if(this.triggeringEventOccurrence instanceof CallEventOccurrence){
			CallEventOccurrence callEventOccurrence = (CallEventOccurrence) this.triggeringEventOccurrence;
			Behavior behavior = this.wrappedExecution.getBehavior();
			List<ParameterValue> outputParameterValues = this.wrappedExecution.getOutputParameterValues();
			if(behavior.outputParameters().size() == outputParameterValues.size()){
				int i = 1;
				List<Parameter> behaviorOutputParameters = callEventOccurrence.execution.getBehavior().outputParameters();
				while(i <= behaviorOutputParameters.size()){
					ParameterValue parameterValue = new ParameterValue();
					parameterValue.parameter = behaviorOutputParameters.get(i - 1);
					parameterValue.values = outputParameterValues.get(i - 1).values;
					callEventOccurrence.execution.setParameterValue(parameterValue);
					i++;
				}
			}
		}
		this._endIsolation();
	}
	
	@Override
	public Value new_() {
		return new EventTriggeredExecution();
	}

	@Override
	public Value copy() {
		EventTriggeredExecution copy = (EventTriggeredExecution) super.copy();
		copy.triggeringEventOccurrence = this.triggeringEventOccurrence;
		copy.wrappedExecution = (Execution)this.wrappedExecution.copy();
		return copy;
	}
}
