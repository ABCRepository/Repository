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
 *  Jeremie Tatibouet (CEA LIST) - Apply fix for FUML12-33 Extensional values should have an unique identifier
 *
 *****************************************************************************/
package org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel;

import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.Locus;

public abstract class ExtensionalValue extends CompoundValue {

	/*
	 * Issue FUML12-33 Extensional values should have an unique identifier
	 * identifier (unique IF) of an extensional value 
	 */
	public String identifier;
	
	/*
	 * The locus of the extent of which this value is a member. (If the value
	 * has been destroyed, it has no locus.)
	 */
	public Locus locus;

	public void destroy() {
		// Remove this value from its locus (if it has not already been
		// destroyed).
		if (this.locus != null) {
			this.locus.remove(this);
		}
	}

	@Override
	public Value copy() {
		// Create a new extensional value with the same feature values at the
		// same locus as this one.
		ExtensionalValue newValue = (ExtensionalValue) (super.copy());
		if (this.locus != null) {
			this.locus.add(newValue);
		}
		return newValue;
	}
	
	@Override
	public String toString() {
		// Issue FUML12-33 Extensional values should have an unique identifier
		return this.identifier + super.toString();
	}
}
