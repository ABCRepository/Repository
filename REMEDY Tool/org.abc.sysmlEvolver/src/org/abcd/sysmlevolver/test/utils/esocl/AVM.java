package org.abcd.sysmlevolver.test.utils.esocl;

import java.util.List;

import org.abc.constraint.ConstraintInstance;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.BooleanValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.EnumerationValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.IntegerValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.RealValue;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Type;

public class AVM extends Search {

	private int iteration;

	@Override
	public List<ParameterValue> getSolution(ConstraintInstance constraint, List<ParameterValue> values) {

		iteration = 0;

		while (!meetStopCriterion()) {

			for (ParameterValue value : values) {

				changeValueUntilNoImprovement(value, 1, constraint);
				changeValueUntilNoImprovement(value, -1, constraint);

				if (constraint.getViolationDistance() == 0) {
					return values;
				}

				iteration++;
			}

		}

		System.err.println("!!solution not found");

		return null;

	}

	private void changeValueUntilNoImprovement(ParameterValue value, int direction, ConstraintInstance constraint) {

		Type type = value.parameter.getType();

		double scale = 0;

		if (type.getName().equals("Integer")) {
			scale = 1;
		} else if (type.getName().equals("Real")) {
			scale = 0.1;
		}

		boolean improved = true;

		int iteration = 0;

		while (improved && (iteration++) < 20000) {

			if (type instanceof PrimitiveType) {

				if (type.getName().equals("Integer")) {

					double originalFitness = constraint.getViolationDistance();

					IntegerValue v = (IntegerValue) value.values.get(0);
					if (v.value + (int) (direction * scale) > value.getMax()
							|| v.value + (int) (direction * scale) < value.getMin()) {
						break;
					} else {
						v.value += (int) (direction * scale);
					}

					double curFitness = constraint.getViolationDistance();

					if (curFitness >= originalFitness) {
						improved = false;

						v.value -= (int) (direction * scale);
					} else {
						scale *= 1.1;
					}

				} else if (type.getName().equals("Real")) {

					double originalFitness = constraint.getViolationDistance();

					RealValue v = (RealValue) value.values.get(0);
					if (v.value + (direction * scale) > value.getMax()
							|| v.value + (direction * scale) < value.getMin()) {
						break;
					} else {
						v.value += direction * scale;
					}

					double curFitness = constraint.getViolationDistance();

					if (curFitness > originalFitness) {
						improved = false;

						v.value -= direction * scale;
					} else {
						scale *= 1.1;
					}

				} else if (type.getName().equals("Boolean")) {

					double originalFitness = constraint.getViolationDistance();

					BooleanValue v = (BooleanValue) value.values.get(0);
					if (direction > 0) {
						if (v.value == true) {
							improved = false;
							break;
						} else {
							v.value = true;
						}
					} else {
						if (v.value == false) {
							improved = false;
							break;
						} else {
							v.value = false;
						}
					}

					double curFitness = constraint.getViolationDistance();

					if (curFitness > originalFitness) {

						if (direction > 0) {
							v.value = false;
						} else {
							v.value = true;
						}
					}

					improved = false;

				}
			} else if (type instanceof Enumeration) {

				double lowestFitness = 999;
				EnumerationLiteral optimal = null;

				EnumerationValue v = (EnumerationValue) value.values.get(0);
				List<EnumerationLiteral> literals = ((Enumeration) type).getOwnedLiterals();
				for (EnumerationLiteral literal : literals) {
					v.literal = literal;
					double curFitness = constraint.getViolationDistance();
					if (curFitness < lowestFitness) {
						lowestFitness = curFitness;
						optimal = literal;
					}
				}

				v.literal = optimal;
				improved = false;
			}
		}

	}

	private boolean meetStopCriterion() {

		return iteration > 50000000;

	}

}
