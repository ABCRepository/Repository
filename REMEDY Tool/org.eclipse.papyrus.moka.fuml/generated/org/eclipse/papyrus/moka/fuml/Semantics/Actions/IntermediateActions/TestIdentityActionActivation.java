/*****************************************************************************
 * Copyright (c) 2012 CEA LIST.
 *
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  CEA LIST - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions;

import org.eclipse.papyrus.moka.fuml.Semantics.Actions.BasicActions.ActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.BooleanValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.RealValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;
import org.eclipse.uml2.uml.TestIdentityAction;

public class TestIdentityActionActivation extends ActionActivation {

	@Override
	public void doAction() {
		// Get the values from the first and second input pins and test if they
		// are equal. (Note the equality of references is defined to be that
		// they have identical referents.)
		// If they are equal, place true on the pin execution for the result
		// output pin, otherwise place false.
		TestIdentityAction action = (TestIdentityAction) (this.node);
		Value firstValue = this.takeTokens(action.getFirst()).get(0);
		Value secondValue = this.takeTokens(action.getSecond()).get(0);
		Value testResult = this.makeBooleanValue(firstValue.equals(secondValue));
		
		
		if(firstValue instanceof RealValue && secondValue instanceof RealValue){
			double dif = Math.abs(((RealValue)firstValue).value - ((RealValue)secondValue).value);
			if(dif < 0.5)
				((BooleanValue)testResult).value = true;
			else
				((BooleanValue)testResult).value = false;
		}
		
		
		this.putToken(action.getResult(), testResult);
	}
}
