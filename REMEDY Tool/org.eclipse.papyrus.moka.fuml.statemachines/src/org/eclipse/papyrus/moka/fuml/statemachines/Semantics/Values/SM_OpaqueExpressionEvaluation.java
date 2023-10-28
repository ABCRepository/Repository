/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Jeremie Tatibouet (CEA LIST)
 *  Arnaud Cuccuru (CEA LIST)
 *
 *****************************************************************************/
package org.eclipse.papyrus.moka.fuml.statemachines.Semantics.Values;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrus.moka.composites.Semantics.Classes.Kernel.CS_OpaqueExpressionEvaluation;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Object_;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.SignalEventOccurrence;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.CommonBehavior.CallEventOccurrence;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.OpaqueExpression;
import org.eclipse.uml2.uml.Parameter;

public class SM_OpaqueExpressionEvaluation extends CS_OpaqueExpressionEvaluation {

	// The context is basically the execution context of the state-machine.
	// This provides the possibility for the behavior associated to the evaluated
	// opaque expression to access features available at the context.
	public Object_ context;

	// Input parameters for the execution of the behavior
	protected List<ParameterValue> parameterValues;
	
	public SM_OpaqueExpressionEvaluation() {
		this.context = null;
		this.parameterValues = new ArrayList<ParameterValue>();
	}
	
	public void initialize(EventOccurrence eventOccurrence){
		this.parameterValues.clear();
		OpaqueExpression expression = (OpaqueExpression)this.specification;
		if(expression.getBehavior().getOwnedParameters().size() > 0){
			Behavior behavior = expression.getBehavior();
			if(eventOccurrence instanceof SignalEventOccurrence){
				SignalEventOccurrence signalEventOccurrence = (SignalEventOccurrence) eventOccurrence;
				if(behavior.inputParameters().size() == 1){
					Parameter parameter = behavior.inputParameters().get(0);
					ParameterValue parameterValue = new ParameterValue();
					parameterValue.parameter = parameter;
					List<Value> values = new ArrayList<Value>();
					values.add(signalEventOccurrence.signalInstance);
					parameterValue.values = values;
					this.setParameterValue(parameterValue);
				}
			}else if(eventOccurrence instanceof CallEventOccurrence){
				CallEventOccurrence callEventOccurrence = (CallEventOccurrence) eventOccurrence;
				List<Parameter> behaviorInputParameters = behavior.inputParameters();
				List<ParameterValue> inputParameterValues = callEventOccurrence.execution.getInputParameterValues();
				if(behaviorInputParameters.size() == inputParameterValues.size()){
					int i = 1;
					while(i <= behaviorInputParameters.size()){
						ParameterValue parameterValue = new ParameterValue();
						parameterValue.parameter = behaviorInputParameters.get(i - 1);
						parameterValue.values = inputParameterValues.get(i - 1).values;
						this.setParameterValue(parameterValue);
						i++;
					}
				}
			}
		}
	}
	
	@Override
	public List<Value> executeExpressionBehavior() {
		// An opaque expression can have an associated behavior. If this is the case
		// this behavior is executed. Values produced by the execution of the behavior
		// are the result of the evaluation of the opaque expression 
		List<Value> evaluation = new ArrayList<Value>();
		OpaqueExpression expression = (OpaqueExpression)this.specification;
		Behavior behavior = expression.getBehavior();
		if(behavior != null) {
			List<ParameterValue> results = this.locus.executor.execute(behavior, context, this.parameterValues);
			for(int i = 0; i < results.size(); i++) { // results.size should be 1
				ParameterValue parameterValue = results.get(i);
				List<Value> values = parameterValue.values;
				for(int j = 0; j < values.size(); j++) {
					evaluation.add(values.get(j));
				}
			}
		}
		return evaluation;
	}
	
	public void setParameterValue(ParameterValue parameterValue){
		// If this parameter value does not exist then it is added
		// to the list parameter values
		if(this.getParameterValue(parameterValue.parameter) == null){
			this.parameterValues.add(parameterValue);
		}
	}
	
	public ParameterValue getParameterValue(Parameter parameter){
		// Return the parameter value corresponding to the given parameter.
		// Null is returned if no parameter value associated with the parameter
		// is found.
		int i = 0;
		ParameterValue parameterValue = null;
		while(parameterValue == null && i < this.parameterValues.size()){
			if(this.parameterValues.get(i).parameter == parameter){
				parameterValue = this.parameterValues.get(i);
			}
			i++;
		}
		return parameterValue;
	}
}
