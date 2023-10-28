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
 *****************************************************************************/
package org.eclipse.papyrus.moka.fuml.statemachines.Semantics.CommonBehavior;

import org.eclipse.papyrus.moka.async.fuml.Semantics.CommonBehaviors.Communications.AsyncObjectActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.GetNextEventStrategy;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.ObjectActivation;

public class LIFOGetNextEventStrategy extends GetNextEventStrategy {

	@Override
	public EventOccurrence getNextEvent(ObjectActivation objectActivation) {
//		EventOccurrence eventOccurrence = null;
//		if(objectActivation.eventPool.size() > 0){
//			int index = objectActivation.eventPool.size() - 1;
//			eventOccurrence = objectActivation.eventPool.get(index);
//			objectActivation.eventPool.remove(index);
//		}
//		return eventOccurrence;
		return ((AsyncObjectActivation)objectActivation).getNextEvent();
	}

}
