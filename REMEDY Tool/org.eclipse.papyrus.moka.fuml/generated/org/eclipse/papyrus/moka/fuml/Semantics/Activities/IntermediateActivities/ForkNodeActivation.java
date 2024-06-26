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
package org.eclipse.papyrus.moka.fuml.Semantics.Activities.IntermediateActivities;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrus.moka.fuml.debug.Debug;

public class ForkNodeActivation extends ControlNodeActivation {

	@Override
	public void fire(List<Token> incomingTokens) {
		// Create forked tokens for all incoming tokens and offer them on all
		// outgoing edges.
		if (this.node == null) {
			Debug.println("[fire] Anonymous fork node.");
		} else {
			Debug.println("[fire] Fork node " + this.node.getName() + "...");
		}
		List<ActivityEdgeInstance> outgoingEdges = this.outgoingEdges;
		int outgoingEdgeCount = outgoingEdges.size();
		List<Token> forkedTokens = new ArrayList<Token>();
		for (int i = 0; i < incomingTokens.size(); i++) {
			Token token = incomingTokens.get(i);
			ForkedToken forkedToken = new ForkedToken();
			forkedToken.baseToken = token;
			forkedToken.remainingOffersCount = outgoingEdgeCount;
			forkedToken.baseTokenIsWithdrawn = false;
			forkedTokens.add(forkedToken);
		}
		this.addTokens(forkedTokens);
		this.sendOffers(forkedTokens);
	}

	@Override
	public void terminate() {
		// Remove any offered tokens and terminate.
		this.clearTokens();
		super.terminate();
	}
}
