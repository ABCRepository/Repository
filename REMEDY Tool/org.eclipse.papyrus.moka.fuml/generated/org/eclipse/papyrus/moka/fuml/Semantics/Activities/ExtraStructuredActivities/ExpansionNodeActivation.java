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
package org.eclipse.papyrus.moka.fuml.Semantics.Activities.ExtraStructuredActivities;

import java.util.List;

import org.eclipse.papyrus.moka.fuml.Semantics.Activities.IntermediateActivities.ObjectNodeActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Activities.IntermediateActivities.Token;
import org.eclipse.papyrus.moka.fuml.debug.Debug;
import org.eclipse.uml2.uml.ExpansionNode;
import org.eclipse.uml2.uml.ExpansionRegion;

public class ExpansionNodeActivation extends ObjectNodeActivation {

	@Override
	public void fire(List<Token> incomingTokens) {
		// Take tokens from all incoming edges.
		Debug.println("[fire] Expansion node " + this.node.getName() + "...");
		this.addTokens(incomingTokens);
	}

	@Override
	public void receiveOffer() {
		// Forward the offer on to the expansion region.
		this.getExpansionRegionActivation().receiveOffer();
	}

	@Override
	public Boolean isReady() {
		// An expansion node is always fired by its expansion region.
		return false;
	}

	public ExpansionRegionActivation getExpansionRegionActivation() {
		// Return the expansion region activation corresponding to this
		// expansion node, in the context of the activity node activation group
		// this expansion node activation is in.
		ExpansionNode node = (ExpansionNode) (this.node);
		ExpansionRegion region = node.getRegionAsInput();
		if (region == null) {
			region = node.getRegionAsOutput();
		}
		return (ExpansionRegionActivation) (this.group.getNodeActivation(region));
	}
}
