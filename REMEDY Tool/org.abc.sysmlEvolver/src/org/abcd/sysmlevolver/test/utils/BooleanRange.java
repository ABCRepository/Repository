package org.abcd.sysmlevolver.test.utils;

import java.util.Random;

import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.BooleanValue;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.uml2.uml.Parameter;

public class BooleanRange extends Range{
	
	private Boolean equal = null;
	private Boolean inequal = null;
	
	@Override
	public void setEqualValue(String value) {
		equal = Boolean.parseBoolean(value);
	}
	@Override
	public void setInequalValue(String value) {
		inequal = Boolean.parseBoolean(value);
	}
	@Override
	public void setMinValue(String value) {
		
	}
	@Override
	public void setMaxValue(String value) {
		
	}
	
	@Override
	public ParameterValue getValidValue(Parameter p, Random random) {
		ParameterValue pv = new ParameterValue();
		pv.parameter = p;
		
		BooleanValue v = new BooleanValue();
		
		if(equal != null){
			v.value = equal;
		}
		else {
			v.value = !inequal;
		}
		pv.values.add(v);
		return pv;
	}
	@Override
	public double getMinValue() {
		
		return 0;
	}
	@Override
	public double getMaxValue() {
		
		return 1;
	}
	@Override
	public boolean isSingle() {
		return true;
	}
	
}
