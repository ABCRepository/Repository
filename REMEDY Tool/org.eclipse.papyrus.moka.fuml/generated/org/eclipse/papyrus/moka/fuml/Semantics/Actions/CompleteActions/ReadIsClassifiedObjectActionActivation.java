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
 *  Jeremie Tatibouet (CEA LIST) - FUML12-34 AcceptEventActionActivation::match should match instances of descendants of a trigger's signal
 *
 *****************************************************************************/
package org.eclipse.papyrus.moka.fuml.Semantics.Actions.CompleteActions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrus.moka.fuml.Semantics.Actions.BasicActions.ActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.ReadIsClassifiedObjectAction;

public class ReadIsClassifiedObjectActionActivation extends ActionActivation {

	@Override
	public void doAction() {
		// Get the value on the object input pin and determine if it is
		// classified by the classifier specified in the action.
		// If the isDirect attribute of the action is false, then place true on
		// the result output pin if the input object has the specified
		// classifier or of one its (direct or indirect) descendants as a type.
		// If the isDirect attribute of the action is true, then place true on
		// the result output pin if the input object has the specified
		// classifier as a type.
		// Otherwise place false on the result output pin.
		ReadIsClassifiedObjectAction action = (ReadIsClassifiedObjectAction) (this.node);
		Value input = this.takeTokens(action.getObject()).get(0);
		List<Classifier> types = input.getTypes();
		boolean result = false;
		int i = 1;
		while (!result & i <= types.size()) {
			Classifier type = types.get(i - 1);
			if (type == action.getClassifier()) {
				result = true;
			} else if (!action.isDirect()) {
				result = this.checkAllParents(type, action.getClassifier());
			}
			i = i + 1;
		}
		List<Value> values = new ArrayList<Value>();
		values.add(this.makeBooleanValue(result));
		this.putTokens(action.getResult(), values);
	}
}
