package org.abcd.sysmlevolver.test.invocation;

import java.util.ArrayList;
import java.util.List;

import org.abcd.sysmlevolver.model.loci.ActiveObjectActivation;
import org.abcd.sysmlevolver.test.log.TestLogger;
import org.abcd.sysmlevolver.test.sut.SUTProxy;
import org.abcd.sysmlevolver.test.sut.SUTConnector.MsgType;
import org.abcd.sysmlevolver.test.utils.TestInputGenerator;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.TransitionActivation;
import org.eclipse.uml2.uml.CallEvent;
import org.eclipse.uml2.uml.ChangeEvent;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Transition;

public class Invocation {

	public ActiveObjectActivation objectActivation = null;
	public TransitionActivation transitionActivation;
	public Operation operation = null;
	public Constraint guard = null;
	public ChangeEvent faultEvent;

	public List<ParameterValue> lastTestInputs = null;


	public Invocation(Invocation inv) {
		this.objectActivation = inv.objectActivation;
		this.transitionActivation = inv.transitionActivation;
		this.operation = inv.operation;
		this.guard = inv.guard;
		this.faultEvent = inv.faultEvent;
		if (inv.lastTestInputs != null) {
			this.lastTestInputs = new ArrayList<ParameterValue>();
			for (ParameterValue value : inv.lastTestInputs) {
				ParameterValue copy = value.copy();
				this.lastTestInputs.add(copy);
			}
		}
	}

	public boolean isFaultInjection() {
		return faultEvent != null;
	}

	public Invocation(ActiveObjectActivation objectActivation, TransitionActivation transitionActivation,
			CallEvent event) {
		this.objectActivation = objectActivation;
		this.transitionActivation = transitionActivation;

		Transition transition = (Transition) transitionActivation.getNode();
		this.operation = ((CallEvent) event).getOperation();
		this.guard = transition.getGuard();
		this.faultEvent = null;

	}

	public Invocation(ActiveObjectActivation objectActivation, TransitionActivation transitionActivation,
			ChangeEvent event) {
		this.objectActivation = objectActivation;
		this.transitionActivation = transitionActivation;

		Stereotype stereotype = event.getAppliedStereotype("healing::Fault");
		List<Operation> operations = (List<Operation>) event.getValue(stereotype, "injectionOperation");
		List<Constraint> conditions = (List<Constraint>) event.getValue(stereotype, "condition");
		this.operation = operations.get(0);
		if (!conditions.isEmpty()) {
			this.guard = conditions.get(0);
		}
		this.faultEvent = event;
	}


	public void invoke(List<ParameterValue> inputs) {

		lastTestInputs = inputs;
		List<ParameterValue> copy = new ArrayList<ParameterValue>();
		for (ParameterValue pv : lastTestInputs) {
			copy.add(pv.copy());
		}
		objectActivation.callOperation(operation, copy);
		TestLogger.log(this);

	}

	public void invoke(boolean newInput) {
		System.err.println("operation   :" + operation);
		if (newInput) {
			if (guard == null) {
				lastTestInputs = new ArrayList<ParameterValue>();
			} else {
				lastTestInputs = TestInputGenerator.instance().generateTestInput(guard, operation.getOwnedParameters());
			}

			List<ParameterValue> copy = new ArrayList<ParameterValue>();
			for (ParameterValue pv : lastTestInputs) {
				copy.add(pv.copy());
			}
			objectActivation.callOperation(operation, copy);

			TestLogger.log(this);
		} else {

			List<ParameterValue> copy = new ArrayList<ParameterValue>();
			if (lastTestInputs != null) {
				for (ParameterValue pv : lastTestInputs) {
					copy.add(pv.copy());
				}
			}

			objectActivation.callOperation(operation, copy);

			TestLogger.log(this);

		}

	}

	public void invoke(boolean newInput, Operation operation, List<ParameterValue> lastTestInputs) {
		if (newInput) {
			List<ParameterValue> copy = new ArrayList<ParameterValue>();
			for (ParameterValue pv : lastTestInputs) {
				copy.add(pv.copy());
			}

			String host = "192.168.1.104";
			String port = "20008";
			String cmd = copy.get(0).parameter.getName() + ":" + copy.get(0).values.get(0); // Format:
																							// thr:0.06
			boolean succ = SUTProxy.instance().send(host, port, cmd);
			if (!succ) {
				return;
			}
			String res = SUTProxy.instance().receive(host, port, MsgType.MSG_TYPE_STATUS);
			if (res == null) {
				return;
			}

			objectActivation.callOperation(operation, copy);
			TestLogger.log(this);
		} else {

			List<ParameterValue> copy = new ArrayList<ParameterValue>();
			if (lastTestInputs != null) {
				for (ParameterValue pv : lastTestInputs) {
					copy.add(pv.copy());
				}
			}

			objectActivation.callOperation(operation, copy);
			TestLogger.log(this);

		}

	}

	

	public double[] getLastTestInput() {

		if (lastTestInputs.isEmpty()) {
			return null;
		}

		double[] inputs = new double[lastTestInputs.size()];
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] = lastTestInputs.get(i).getValue();
		}
		return inputs;
	}


	public String toString() {
		StringBuffer str = new StringBuffer();

		if (isFaultInjection()) {
			if (guard != null) {
				str.append("fault injection: ");
				str.append(objectActivation.getName());
				str.append(".");
				str.append(operation.getName());
				str.append("()[");
				str.append(guard.getSpecification().stringValue());
				str.append("]");
			} else {
				str.append("fault injection: ");
				str.append(objectActivation.getName());
				str.append(".");
				str.append(operation.getName());
				str.append("()[]");
			}
		} else {
			if (guard != null) {
				str.append(objectActivation.getName());
				str.append(".");
				str.append(operation.getName());
				str.append("()[");
				str.append(guard.getSpecification().stringValue());
				str.append("]");
			} else {
				str.append(objectActivation.getName());
				str.append(".");
				str.append(operation.getName());
				str.append("()[]");
			}
		}

		str.append("   (");
		str.append(lastTestInputs);
		str.append("), ");

		return str.toString();
	}

}
