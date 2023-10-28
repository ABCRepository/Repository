package org.abcd.sysmlevolver.model.Semantics;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.abc.constraint.ConstraintInstance;
import org.abc.constraint.ConstraintInstanceFactory;
import org.abcd.sysmlevolver.ExecutionParameters;
import org.abcd.sysmlevolver.debug.TestControlDelegate;
import org.abcd.sysmlevolver.model.loci.ActiveObjectActivation;
import org.abcd.sysmlevolver.test.log.PerformanceLogger;
import org.abcd.sysmlevolver.test.log.TestLogger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrus.moka.fuml.FUMLExecutionEngine;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.CommonBehavior.CallEventOccurrence;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.ExternalTransitionActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.FinalStateActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.RegionActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateConfiguration;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateMachineExecution;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.VertexActivation;
import org.eclipse.uml2.uml.ChangeEvent;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Vertex;

public class SysMLEvolverExternalTransitionActivation extends ExternalTransitionActivation {

	@Override
	public boolean evaluateGuard(EventOccurrence eventOccurrence) {

		Transition transition = (Transition) this.node;
		Constraint guard = transition.getGuard();
		if (guard != null) {
			ConstraintInstance guardEvaluation = ConstraintInstanceFactory.instance().createConstraintInstance(guard,
					eventOccurrence);
			boolean res = guardEvaluation.evaluate();
			res = guardEvaluation.evaluate();
			return res;
		} else {
			return true;
		}

	}

	private void updateConstraintList(SysMLEvolverStateActivation stateActivation) {

		StateMachineExecution execution = (StateMachineExecution) this.getStateMachineExecution();
		StateConfiguration root = execution.getConfiguration().getRoot();

		StateConfiguration configuration = getStateConfiguration(root, stateActivation);
		if (configuration != null) {
			removeConstraint(configuration);
		} else {
			System.err.println(stateActivation + "is not in state configuration");
		}
	}

	private void removeConstraint(StateConfiguration configuration) {
		VertexActivation activation = configuration.getVertexActivation();
		

		for (StateConfiguration child : configuration.getChildren()) {
			removeConstraint(child);
		}
	}

	private StateConfiguration getStateConfiguration(StateConfiguration configuration,
			SysMLEvolverStateActivation stateActivation) {

		if (stateActivation.equals(configuration.getVertexActivation())) {
			return configuration;
		}

		for (StateConfiguration child : configuration.getChildren()) {
			StateConfiguration res = getStateConfiguration(child, stateActivation);
			if (res != null) {
				return res;
			}
		}

		return null;
	}

	@Override
	protected void exitSource(EventOccurrence eventOccurrence) {

		if (vertexSourceActivation instanceof SysMLEvolverStateActivation) {
			updateConstraintList((SysMLEvolverStateActivation) vertexSourceActivation);
		}

		long beginTime = System.nanoTime();

		super.exitSource(eventOccurrence);

		long stopTime = System.nanoTime();

		if (ExecutionParameters.logPerformance) {
			PerformanceLogger.logExitState(stopTime - beginTime);
		}

		TestLogger.log(this.vertexSourceActivation, true);

		NamedElement node = this.vertexSourceActivation.getNode();
		if (!(node instanceof State)) {
			return;
		}
		

	}

	@Override
	protected void enterTarget(EventOccurrence eventOccurrence) {

		
		if (this.vertexTargetActivation.isEnterable(this, false)) {
			if (eventOccurrence instanceof CallEventOccurrence) {
				wait4stateUpdate();
			}

			TestLogger.log(this.vertexTargetActivation, false);

			long beginTime = System.nanoTime();

			this.vertexTargetActivation.enter(this, eventOccurrence, this.getLeastCommonAncestor());

			long stopTime = System.nanoTime();

			if (ExecutionParameters.logPerformance) {
				PerformanceLogger.logEnterState(stopTime - beginTime);
			}

		} else {
			if (this.vertexTargetActivation instanceof StateActivation) {
				StateActivation targetStateActivation = (StateActivation) this.vertexTargetActivation;
				int i = 0;
				RegionActivation containingRegionActivation = null;
				while (containingRegionActivation == null && i < targetStateActivation.regionActivation.size()) {
					RegionActivation currentActivation = targetStateActivation.regionActivation.get(i);
					if (currentActivation.getVertexActivation((Vertex) this.vertexSourceActivation.getNode()) != null) {
						containingRegionActivation = currentActivation;
					}
					i++;
				}
				if (containingRegionActivation != null) {
					containingRegionActivation.isCompleted = true;
					if (targetStateActivation.hasCompleted()) {
						targetStateActivation.notifyCompletion();
					}
				}
			}

		}
	}

	private Instant startWaitingTime = null;

	private void wait4stateUpdate() {

		if (ExecutionParameters.simulating) {
			return;
		}

		startWaitingTime = Instant.now();

		if (vertexTargetActivation instanceof FinalStateActivation) {
			return;
		}

		if (vertexTargetActivation instanceof StateActivation) {

			ConstraintInstance invariant = ((SysMLEvolverStateActivation) vertexTargetActivation).stateInvariant;
			if (invariant == null) {
				return;
			}

			int true_times = 0;
			while (true) {
				if (invariant.evaluate()) {
					true_times++;

					if (true_times > 10) {
						ActiveObjectActivation activation = (ActiveObjectActivation) ((ActiveObject) this
								.getExecutionContext()).objectActivation;
						activation.setTraversing(false);
						break; // target state reached
					}

				} else {
					true_times = 0;
				}

				ActiveObjectActivation activation = (ActiveObjectActivation) ((ActiveObject) this
						.getExecutionContext()).objectActivation;
				activation.setTraversing(true);
				synchronized (activation) {
					try {
						activation.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				Instant now = Instant.now();
				long nanos = Duration.between(startWaitingTime, now).toNanos();
				long ms = nanos / 1000000;
				if (ms > 1800000) { 
					((TestControlDelegate) FUMLExecutionEngine.eInstance.getControlDelegate())
							.notifyEnteringTargetStateTimeout((StateActivation) vertexTargetActivation);
				}
			}

		}
	}

}
