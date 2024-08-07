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
package org.eclipse.papyrus.moka.fuml.statemachines.Semantics.Loci;

import org.eclipse.papyrus.moka.async.fuml.Semantics.Loci.LociL1.AsyncLocus;
import org.eclipse.papyrus.moka.composites.Semantics.Loci.LociL3.CS_Locus;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Object_;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StructuredClassifiers.SM_Object;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Class;

public class SM_Locus extends AsyncLocus {
	
	public Object_ instantiate(Class type) {
		Object_ object = null;
		if (type instanceof Behavior) {
			object = super.instantiate(type);
		} else {
			object = new SM_Object();
			object.types.add(type);
			object.createFeatureValues();
			this.add(object);
		}
		return object;
	}

	
}
