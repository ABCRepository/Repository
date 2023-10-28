package org.abc.constraint;

import org.abc.constraint.node.ConstraintNode;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.ValueSpecification;

public class BooleanValueSpecificationInstance {
	
	private ValueSpecification specification;
	private ConstraintNode validator;
	
	public BooleanValueSpecificationInstance(ValueSpecification specification, ConstraintNode validator){
		this.specification = specification;
		this.validator = validator;
	}
	
	public boolean evaluate() {
		
		return (Boolean)validator.getValue();
	}
	
	public String toString(){
		return specification.toString();
	}
	
}
