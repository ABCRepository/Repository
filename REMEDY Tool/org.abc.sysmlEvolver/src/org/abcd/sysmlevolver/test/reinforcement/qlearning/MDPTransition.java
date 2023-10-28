package org.abcd.sysmlevolver.test.reinforcement.qlearning;

import java.util.List;

import org.abcd.sysmlevolver.model.Semantics.SysMLEvolverExternalTransitionActivation;
import org.abcd.sysmlevolver.model.loci.ActiveObjectActivation;
import org.abcd.sysmlevolver.test.invocation.Invocation;
import org.eclipse.uml2.uml.CallEvent;
import org.eclipse.uml2.uml.ChangeEvent;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Event;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Trigger;

public class MDPTransition {

	public SysMLEvolverExternalTransitionActivation transitinoActivation;

	public MDPState source;
	public MDPState target;

	private double q;

	private QIntervalValue faultDetectionProbability;

	public Invocation invocation;

	public boolean useShadow = false;

	public int transitionVisitedNum;

	public MDPTransition(SysMLEvolverExternalTransitionActivation transitionActivation, MDPState source,
			MDPState target) {
		this.transitinoActivation = transitionActivation;
		this.source = source;
		this.target = target;
		this.transitionVisitedNum = 1;

		this.faultDetectionProbability = new QIntervalValue();
		this.q = QLearningTester.initialReward;

		if (transitionActivation == null) {
			invocation = null;
			return;
		}

		ActiveObjectActivation objectActivation = (ActiveObjectActivation) transitionActivation
				.getExecutionContext().objectActivation;
		Transition transition = (Transition) transitionActivation.getNode();
		Trigger trigger = transition.getTriggers().get(0);
		Event event = trigger.getEvent();
		if (event instanceof CallEvent) {

			invocation = new Invocation(objectActivation, transitionActivation, (CallEvent) event);
			if (invocation.operation.getName().equals("stop")) {
				q = 0;
			} //
		}

	}

	public boolean isFaultInjection() {
		return invocation.isFaultInjection();
	}

	public Invocation getInvocation() {
		return invocation;
	}

	private double getReward() {

		if (faultDetectionProbability.getSampleSize() == 0) {
			return QLearningTester.initialReward;
		} else {
			return faultDetectionProbability.getMax();
		}

	}

	public void onPolicyUpdateQ(double fdp, double qValueOfNextAction) {

		double reward = getReward();
		if (invocation.isFaultInjection()) { // fault injection is the last
												// action
			q = reward;
		} else if (invocation.operation.getName().equals("stop")) {
			q = 0;
		} else {

			double nextQ = reward + QLearningTester.discountFactor * qValueOfNextAction;
			double currQ = reward > nextQ ? reward : nextQ;
			q = q + QLearningTester.learningRate * (nextQ - q);// +
		}
	}

	public void updateQ() {

		double reward = getReward();
		if (invocation.isFaultInjection()) { // fault injection is the last
												// action
			q = reward;
		} else if (invocation.operation.getName().equals("stop")) {
			q = 0;
		} else {
			double nextQ = QLearningTester.discountFactor * target.getQ(useShadow);
			double currQ = reward > nextQ ? reward : nextQ;

			q = q + QLearningTester.learningRate * (currQ - q);
		}

	}

	public void updateQ(double fdpSample) {


		updateQ();

	}

	public double getCoverage() {
		return faultDetectionProbability.getSampleSize();
	}

	public double getQ() {

		return q;


	}

	public String getOperationName() {
		if (invocation == null)
			return "";
		else
			return invocation.operation.getName();
	}

	public void updateInvocation4NewTest(SysMLEvolverExternalTransitionActivation newTransitionActivation) {

		this.transitinoActivation = newTransitionActivation;

		if (transitinoActivation == null) {
			return;
		}

		invocation.objectActivation = (ActiveObjectActivation) newTransitionActivation
				.getExecutionContext().objectActivation;
		invocation.transitionActivation = newTransitionActivation;

		Transition transition = (Transition) newTransitionActivation.getNode();
		Trigger trigger = transition.getTriggers().get(0);
		Event event = trigger.getEvent();
		if (event instanceof CallEvent) {

			invocation.operation = ((CallEvent) event).getOperation();
			invocation.guard = transition.getGuard();

		} //

	}

	public boolean isNullTransition() {

		return this.transitinoActivation == null;

	}

	public boolean isCovered() {
		return faultDetectionProbability.getSampleSize() > 0;
	}

	public String toString() {

		StringBuffer str = new StringBuffer();

		if (this.transitinoActivation == null) {
			str.append("*Null transition ");
		} else {
			str.append(this.invocation.operation.getName() + " ");
		}
		// str.append("S" + source.id + " -> ");
		str.append("State");
		str.append(target.id);
		str.append(" q(");
		str.append(q);
		str.append(") v");
		str.append(faultDetectionProbability);

		return str.toString();
	}

}
