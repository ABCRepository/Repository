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
package org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines;

import java.util.List;

import org.eclipse.papyrus.moka.async.fuml.Semantics.CommonBehaviors.Communications.AsyncObjectActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.uml2.uml.Class;

public class DoActivityContextObjectActivation extends AsyncObjectActivation {
	
	
	
	public DoActivityContextObjectActivation(Class classifier, List<ParameterValue> inputs) {
		super(classifier, inputs);
	}

	@Override
	public void dispatchNextEvent() {
		// The dispatching behaves exactly the same at it was specified in fUML.
		// In addition to this behavior the dispatch sequence of an object activation
		// for a DoActivityContextObject has the capacity to notify the state having
		// triggered if the executed doActivity has completed. The completion of a
		// do activity is determined based on the absence of any registered event
		// accepter after the current step.
		super.dispatchNextEvent();
		if(this.waitingEventAccepters.size() == 0){
			DoActivityContextObject doActivityObject = (DoActivityContextObject)this.object;
			if(doActivityObject.owner!=null){
				doActivityObject.owner.isDoActivityCompleted = true;
				if(doActivityObject.owner.hasCompleted()){
					doActivityObject.owner.notifyCompletion();
				}
			}
		}
	}
}
