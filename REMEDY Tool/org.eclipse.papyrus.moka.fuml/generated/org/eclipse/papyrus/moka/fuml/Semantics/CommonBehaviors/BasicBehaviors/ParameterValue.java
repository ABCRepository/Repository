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
package org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.BooleanValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.IntegerValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.RealValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;

public class ParameterValue {

	public Parameter parameter;

	private double min = 0;
	private double max = 0;

	/*
	 * The values of for the parameter. Zero or more values are possible, as
	 * constrained by the multiplicity of the parameter.
	 */
	public List<Value> values = null;

	public ParameterValue() {
		values = new ArrayList<Value>();
	}

	public double getMin() {
		if (min == 0 && max == 0) {
			initRange();
		}
		return min;
	}

	public double getMax() {
		if (min == 0 && max == 0) {
			initRange();
		}
		return max;
	}

	public void setValue(Parameter p, double v) {
		parameter = p;

		values.clear();
		Type type = p.getType();
		Value value = null;
		if (type.getName().equals("Integer")) {
			value = new IntegerValue();
			;
			((IntegerValue) value).value = (int) v;
		} else if (type.getName().equals("Real")) {
			value = new RealValue();
			((RealValue) value).value = v;
		} else if (type.getName().equals("Boolean")) {
			value = new BooleanValue();
			if (v == 0) {
				((BooleanValue) value).value = false;
			} else {
				((BooleanValue) value).value = true;
			}
		}
		values.add(value);
	}

	public double getValue() {
		Value value = values.get(0);
		if (value instanceof BooleanValue) {
			if (((BooleanValue) value).value) {
				return 1;
			} else {
				return 0;
			}
		} else if (value instanceof IntegerValue) {
			return ((IntegerValue) value).value;
		} else if (value instanceof RealValue) {
			return ((RealValue) value).value;
		} else {
			System.err.println("parametervalue type error");
			return 0;
		}
	}

	private void initRange() {

		Stereotype r = parameter.getAppliedStereotype("testing::LimitedProperty");
		if (r == null) {
			Type type = parameter.getType();
			if (type.getName().equals("Integer")) {
				// min = Integer.MIN_VALUE;
				// max = Integer.MAX_VALUE;
				min = 0;
				max = 1;
			} else if (type.getName().equals("Real")) {
				// min = -Double.MAX_VALUE;
				// max = Double.MAX_VALUE;
				min = 0;
				max = 1;
			} else {
				min = max = -1;
			}
		} else {
			min = (Double) parameter.getValue(r, "min");
			max = (Double) parameter.getValue(r, "max");
		}

	}

	public ParameterValue copy() {
		// Create a new parameter value for the same parameter as this parameter
		// value, but with copies of the values of this parameter value.
		ParameterValue newValue = new ParameterValue();
		newValue.parameter = this.parameter;
		List<Value> values = this.values;
		for (int i = 0; i < values.size(); i++) {
			Value value = values.get(i);
			newValue.values.add(value.copy());
		}

		newValue.max = max;
		newValue.min = min;

		return newValue;
	}

	public String toString() {
		return parameter.getName() + " = " + values;
	}

}
