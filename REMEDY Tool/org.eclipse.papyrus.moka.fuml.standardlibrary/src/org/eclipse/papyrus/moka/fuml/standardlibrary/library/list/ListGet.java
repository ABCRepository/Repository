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
package org.eclipse.papyrus.moka.fuml.standardlibrary.library.list;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.IntegerValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.OpaqueBehaviorExecution;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.papyrus.moka.fuml.debug.Debug;

public class ListGet extends OpaqueBehaviorExecution {

	@Override
	public void doBody(List<ParameterValue> inputParameters, List<ParameterValue> outputParameters) {
		try {
			Integer index = ((IntegerValue) inputParameters.get(1).values.get(0)).value;
			List<Value> list = inputParameters.get(0).values; // CORRECTED "get(1)" to "get(0)"
			List<Value> outputs = new ArrayList<Value>();
			if (index > 0 && index <= list.size()) { // ADDED if statement
				outputs.add(list.get(index - 1));
			}
			outputParameters.get(0).values = outputs;
		} catch (Exception e) {
			Debug.println("An error occured during the execution of ListGet " + e.getMessage());
		}
	}

	@Override
	public Value new_() {
		return new ListGet();
	}
}
