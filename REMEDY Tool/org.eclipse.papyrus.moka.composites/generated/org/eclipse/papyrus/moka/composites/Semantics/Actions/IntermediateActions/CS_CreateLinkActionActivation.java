/*****************************************************************************
 * Copyright (c) 2013 CEA LIST.
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
package org.eclipse.papyrus.moka.composites.Semantics.Actions.IntermediateActions;

// Imports
import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrus.moka.composites.Semantics.CompositeStructures.StructuredClasses.CS_Link;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions.CreateLinkActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.ExtensionalValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Link;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.UnlimitedNaturalValue;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.CreateLinkAction;
import org.eclipse.uml2.uml.LinkEndCreationData;
import org.eclipse.uml2.uml.LinkEndData;


public class CS_CreateLinkActionActivation extends CreateLinkActionActivation {

	@Override
	public void doAction() {
		// Get the extent at the current execution locus of the association for
		// which a link is being created.
		// Destroy all links that have a value for any end for which
		// isReplaceAll is true.
		// Create a new link for the association, at the current locus, with the
		// given end data values,
		// inserted at the given insertAt position (for ordered ends).
		// fUML semantics is extended in the sense that a CS_Link is created instead of
		// a Link
		CreateLinkAction action = (CreateLinkAction) (this.node);
		List<LinkEndCreationData> endDataList = new ArrayList<LinkEndCreationData>();
		for (LinkEndData data : action.getEndData()) {
			endDataList.add((LinkEndCreationData) data);
		}
		Association linkAssociation = this.getAssociation();
		List<ExtensionalValue> extent = this.getExecutionLocus().getExtent(linkAssociation);
		boolean unique = false;
		for (int i = 0; i < endDataList.size(); i++) {
			if (endDataList.get(i).getEnd().isUnique()) {
				unique = true;
			}
		}
		for (int i = 0; i < extent.size(); i++) {
			ExtensionalValue value = extent.get(i);
			Link link = (Link) value;
			boolean match = true;
			boolean destroy = false;
			int j = 1;
			while (j <= endDataList.size()) {
				LinkEndCreationData endData = endDataList.get(j - 1);
				if (this.endMatchesEndData(link, endData)) {
					if (endData.isReplaceAll()) {
						destroy = true;
					}
				} else {
					match = false;
				}
				j = j + 1;
			}
			if (destroy | unique & match) {
				link.destroy();
			}
		}
		CS_Link newLink = new CS_Link();
		newLink.type = linkAssociation;
		for (int i = 0; i < endDataList.size(); i++) {
			LinkEndCreationData endData = endDataList.get(i);
			int insertAt = 0;
			if (endData.getInsertAt() != null) {
				insertAt = ((UnlimitedNaturalValue) (this.takeTokens(endData.getInsertAt()).get(0))).value;
			}
			newLink.setFeatureValue(endData.getEnd(), this.takeTokens(endData.getValue()), insertAt);
		}
		newLink.addTo(this.getExecutionLocus());
	}
}
