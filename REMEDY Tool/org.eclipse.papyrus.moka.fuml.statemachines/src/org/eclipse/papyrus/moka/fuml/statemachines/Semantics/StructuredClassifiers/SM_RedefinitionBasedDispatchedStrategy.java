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
package org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StructuredClassifiers;

import java.util.List;

import org.eclipse.papyrus.moka.composites.Semantics.CommonBehaviors.Communications.CS_DispatchOperationOfInterfaceStrategy;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Object_;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.Execution;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.CommonBehavior.CallEventExecution;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Operation;

public class SM_RedefinitionBasedDispatchedStrategy extends CS_DispatchOperationOfInterfaceStrategy {

	@Override
	public Execution dispatch(Object_ object, Operation operation) {
		// Try to dispatch the operation has specified in fUML. If no method is defined
		// for the dispatched operation then null is returned. In this situation, the
		// dispatching of the operation is computed as being by with using the
		// call event semantics.
		Execution execution = null;
		Behavior method = this.getMethod(object, operation);
		if(method == null){
			execution = new CallEventExecution();
			execution.context = object;
			((CallEventExecution)execution).operation = operation;
		}else{
			execution = object.locus.factory.createExecution(method, object);
		}
		return execution;
	}
	
	@Override
	public Behavior getMethod(Object_ object, Operation operation) {
		// Behaves as in fUML and PSCS except that if no method is declared
		// for the operation then null is returned.
		Behavior method = null;
		int i = 1;
		while (method == null & i <= object.types.size()) {
			Class type = object.types.get(i - 1);
			List<NamedElement> members = type.getMembers();
			int j = 1;
			while (method == null & j <= members.size()) {
				NamedElement member = members.get(j - 1);
				if (member instanceof Operation) {
					Operation memberOperation = (Operation) member;
					if (this.operationsMatch(memberOperation, operation)) {
						if(memberOperation.getMethods().size() > 0){
							method = memberOperation.getMethods().get(0);
						}
					}
				}
				j = j + 1;
			}
			i = i + 1;
		}
		return method;
	}
	
}
