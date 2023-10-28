package org.abcd.sysmlevolver.test.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.abc.constraint.ConstraintInstance;
import org.abc.constraint.ConstraintInstanceFactory;
import org.abcd.sysmlevolver.ExecutionParameters;
import org.abcd.sysmlevolver.test.log.PerformanceLogger;
import org.abcd.sysmlevolver.test.utils.esocl.AVM;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.BooleanValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.EnumerationValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.IntegerValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.RealValue;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Type;

public class TestInputGenerator {

	private static Random random = new Random();

	private static TestInputGenerator instance = null;

	public List<ParameterValue> generateTestInput(Constraint constraint, List<Parameter> parameters) {
		List<ParameterValue> values;

		if (constraint == null) {
			values = new ArrayList<ParameterValue>();
			for (Parameter p : parameters) {
				values.add(getUnlimitedParameterValue(p));
			}
			return values;
		} else {
			values = getFixedValue(constraint, parameters);
			if (values != null) {
				return values;
			}
		}
		values = new ArrayList<ParameterValue>();
		for (Parameter p : parameters) {
			values.add(getUnlimitedParameterValue(p));
		}

		long beginTime = System.currentTimeMillis();
		ConstraintInstance instance = ConstraintInstanceFactory.instance().createConstraintInstance(constraint, values);
		// AVM avm = new AVM();
		// values = avm.getSolution(instance, values);
		long stopTime = System.currentTimeMillis();

		if (ExecutionParameters.logPerformance) {
			PerformanceLogger.logGenerateTestData(stopTime - beginTime);
		}


		return values;

	}

	public List<Long> times = new ArrayList<Long>();

	public List<ParameterValue> generateTestInput(String constraint, List<Parameter> parameters) {

		List<ParameterValue> values;

		if (constraint == null) {
			return null;
		}

		values = new ArrayList<ParameterValue>();
		for (Parameter p : parameters) {
			values.add(getUnlimitedParameterValue(p));
		}

		long beginTime = System.nanoTime();

		ConstraintInstance instance = ConstraintInstanceFactory.instance().createConstraintInstance(constraint, values);

		AVM avm = new AVM();
		values = avm.getSolution(instance, values);

		long stopTime = System.nanoTime();

		long cost = stopTime - beginTime;


		times.add(cost);

		return values;

	}

	private List<ParameterValue> getFixedValue(Constraint constraint, List<Parameter> parameters) {

		String constraintStr = constraint.getSpecification().stringValue();

		if (constraintStr.contains(">") || constraintStr.contains("<")) {
			return null;
		}
		List<ParameterValue> values = new ArrayList<ParameterValue>();
		String[] items = constraintStr.split("and");
		for (int i = 0; i < items.length; i++) {
			String[] pair = items[i].split("=");
			String paramName = pair[0].trim();
			String value = pair[1].trim();
			ParameterValue parameterValue = new ParameterValue();
			parameterValue.parameter = getParameter(parameters, paramName);

			Type type = parameterValue.parameter.getType();
			if (type.getName().equals("Integer")) {
				IntegerValue v = new IntegerValue();
				v.value = Integer.parseInt(value);
				parameterValue.values.add(v);
			} else if (type.getName().equals("Real")) {
				RealValue v = new RealValue();
				v.value = Double.parseDouble(value);
				parameterValue.values.add(v);
			} else if (type.getName().equals("Boolean")) {
				BooleanValue v = new BooleanValue();
				v.value = Boolean.parseBoolean(value);
				parameterValue.values.add(v);
			}
			values.add(parameterValue);
		}

		return values;

	}

	private Parameter getParameter(List<Parameter> parameters, String paramName) {
		for (Parameter p : parameters) {
			if (p.getName().equals(paramName)) {
				return p;
			}
		}
		return null;
	}

	private ParameterValue getUnlimitedParameterValue(Parameter p) {
		ParameterValue parameterValue = new ParameterValue();
		parameterValue.parameter = p;
		double max = parameterValue.getMax();
		double min = parameterValue.getMin();


		Type type = p.getType();

		if (type instanceof PrimitiveType) {

			if (type.getName().equals("Integer")) {
				IntegerValue v = new IntegerValue();
				v.value = random.nextInt((int) (max - min)) + (int) min;
				parameterValue.values.add(v);
			} else if (type.getName().equals("Real")) {
				RealValue v = new RealValue();
				v.value = random.nextDouble() * (max - min) + min;
				parameterValue.values.add(v);
			} else if (type.getName().equals("Boolean")) {
				BooleanValue v = new BooleanValue();
				v.value = random.nextBoolean();
				parameterValue.values.add(v);
			}
		} else if (type instanceof Enumeration) {

			EnumerationValue v = new EnumerationValue();
			v.type = (Enumeration) type;
			List<EnumerationLiteral> literals = ((Enumeration) type).getOwnedLiterals();
			v.literal = literals.get(random.nextInt(literals.size()));
			parameterValue.values.add(v);
		}

		return parameterValue;
	}

	private ParameterValue getUnlimitedParameterValue(Constraint constraint, Parameter p) {
		String constraintStr = constraint.getSpecification().stringValue();
		if (!constraintStr.contains(">") || !constraintStr.contains("<")) {
			return null;
		}
		ParameterValue parameterValue = new ParameterValue();
		parameterValue.parameter = p;
		String[] items = constraintStr.split("and");
		String[] minString = items[0].split(">");
		double min = Double.parseDouble((minString[1]).trim());
		String[] maxString = items[1].split("<");
		double max = Double.parseDouble((maxString[1]).trim());


		Type type = p.getType();
		if (type instanceof PrimitiveType) {

			if (type.getName().equals("Integer")) {
				IntegerValue v = new IntegerValue();
				v.value = random.nextInt((int) (max - min)) + (int) min;
				parameterValue.values.add(v);
			} else if (type.getName().equals("Real")) {
				RealValue v = new RealValue();
				v.value = random.nextDouble() * (max - min) + min;
				parameterValue.values.add(v);
			} else if (type.getName().equals("Boolean")) {
				BooleanValue v = new BooleanValue();
				v.value = random.nextBoolean();
				parameterValue.values.add(v);
			}
		} else if (type instanceof Enumeration) {
			EnumerationValue v = new EnumerationValue();
			v.type = (Enumeration) type;
			List<EnumerationLiteral> literals = ((Enumeration) type).getOwnedLiterals();
			v.literal = literals.get(random.nextInt(literals.size()));
			parameterValue.values.add(v);
		}
		return parameterValue;
	}

	public static TestInputGenerator instance() {
		if (instance == null) {
			instance = new TestInputGenerator();
		}
		return instance;
	}


}
