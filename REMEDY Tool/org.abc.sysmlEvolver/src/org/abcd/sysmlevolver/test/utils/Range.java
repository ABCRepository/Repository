package org.abcd.sysmlevolver.test.utils;

import java.util.Random;

import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.uml2.uml.Parameter;

public abstract class Range {
	
	public abstract void setEqualValue(String value);
	public abstract void setInequalValue(String value);
	public abstract void setMinValue(String value);
	public abstract void setMaxValue(String value);
	public abstract ParameterValue getValidValue(Parameter p, Random random);
	
	public abstract double getMinValue();
	public abstract double getMaxValue();
	
	public abstract boolean isSingle();
}
