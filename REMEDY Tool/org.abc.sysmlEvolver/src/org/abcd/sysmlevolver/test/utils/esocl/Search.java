package org.abcd.sysmlevolver.test.utils.esocl;

import java.util.List;

import org.abc.constraint.ConstraintInstance;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;

public abstract class Search {
	
	public abstract List<ParameterValue> getSolution(ConstraintInstance constraint, List<ParameterValue> values);
	
}
