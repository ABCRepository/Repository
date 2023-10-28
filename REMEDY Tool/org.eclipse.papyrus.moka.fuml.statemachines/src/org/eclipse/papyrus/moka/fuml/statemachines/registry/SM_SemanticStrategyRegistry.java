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
 *
 *****************************************************************************/
package org.eclipse.papyrus.moka.fuml.statemachines.registry;

import org.eclipse.papyrus.moka.composites.Semantics.CommonBehaviors.Communications.CS_NameBased_StructuralFeatureOfInterfaceAccessStrategy;
import org.eclipse.papyrus.moka.composites.Semantics.CompositeStructures.InvocationActions.CS_DefaultConstructStrategy;
import org.eclipse.papyrus.moka.composites.Semantics.CompositeStructures.InvocationActions.CS_DefaultRequestPropagationStrategy;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions.DefaultCreateObjectActionStrategy;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.IntermediateActions.RestrictiveGetAssociationStrategy;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.FIFOGetNextEventStrategy;
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.FirstChoiceStrategy;
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.Locus;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StructuredClassifiers.SM_RedefinitionBasedDispatchedStrategy;

public class SM_SemanticStrategyRegistry {
	
	public void registerSemanticStrategies(Locus locus) {
		locus.factory.setStrategy(new FirstChoiceStrategy());
		locus.factory.setStrategy(new FIFOGetNextEventStrategy());
		//locus.factory.setStrategy(new LIFOGetNextEventStrategy());
		locus.factory.setStrategy(new SM_RedefinitionBasedDispatchedStrategy());
		locus.factory.setStrategy(new CS_NameBased_StructuralFeatureOfInterfaceAccessStrategy());
		locus.factory.setStrategy(new CS_DefaultRequestPropagationStrategy());
		locus.factory.setStrategy(new CS_DefaultConstructStrategy());
		locus.factory.setStrategy(new RestrictiveGetAssociationStrategy());
		locus.factory.setStrategy(new DefaultCreateObjectActionStrategy());
	}
	
}
