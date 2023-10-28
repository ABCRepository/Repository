package org.abc.constraint;

import org.abc.constraint.node.ConstraintNode;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Stereotype;

public class ConstraintInstance {

	public int violatedTimes = 0;

	private Constraint constraint;
	private ConstraintNode validator;
	private boolean isSafeCondition;

	public ConstraintInstance(Constraint constraint, ConstraintNode validator) {
		if (constraint != null) {
			Stereotype safeCondition = constraint.getAppliedStereotype("");
			if (safeCondition == null) {
				this.isSafeCondition = false;
			} else {
				this.isSafeCondition = true;
			}
		}
		this.constraint = constraint;
		this.validator = validator;
	}

	public boolean isSafeCondition() {
		return isSafeCondition;
	}

	public boolean evaluate() {
		boolean result = (Boolean) validator.getValue();
		return result;
	}

	public double getViolationDistance() {
		double result = validator.getViolationDistance();
		return result;
	}

	public double getSafeDistance() {
		double result = validator.getSafeDistance();
		if (result > 1) {
			result = 1;
		}
		return result;
	}

	public String toString() {
		if (constraint != null) {
			return constraint.getSpecification().stringValue();
		} else {
			return null;
		}
	}

}
