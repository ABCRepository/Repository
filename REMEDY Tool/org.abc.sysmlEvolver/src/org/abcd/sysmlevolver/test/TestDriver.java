package org.abcd.sysmlevolver.test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.abcd.sysmlevolver.ExecutionParameters;
import org.abcd.sysmlevolver.debug.TestControlDelegate;
import org.abcd.sysmlevolver.evolution.updater.DiagramUpdater;
import org.abcd.sysmlevolver.model.Semantics.ActiveObject;
import org.abcd.sysmlevolver.model.Semantics.SysMLEvolverStateActivation;
import org.abcd.sysmlevolver.model.loci.ActiveObjectActivation;
import org.abcd.sysmlevolver.test.invocation.Invocation;
import org.abcd.sysmlevolver.test.log.TestLogger;
import org.abcd.sysmlevolver.test.reinforcement.ReinforcementTester;
import org.abcd.sysmlevolver.test.sut.SUTObject;
import org.abcd.sysmlevolver.test.sut.SUTProxy;
import org.abcd.sysmlevolver.test.utils.FaultySenario;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.papyrus.moka.async.fuml.Semantics.CommonBehaviors.Communications.AsyncObjectActivation.ObjectActivationState;
import org.eclipse.papyrus.moka.fuml.FUMLExecutionEngine;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.ExtensionalValue;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateConfiguration;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateMachineExecution;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.TransitionActivation.TransitionMetadata;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.VertexActivation.StateMetadata;
import org.eclipse.uml2.uml.ChangeEvent;
import org.eclipse.uml2.uml.Pseudostate;
import org.eclipse.uml2.uml.PseudostateKind;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Vertex;

public class TestDriver {

	private boolean testDone = false;

	private ReinforcementTester tester;
	private SUTObject sut;
	private List<FaultySenario> faultySenarios;
	private List<Invocation> invocationSequence;
	private List<Invocation> bestInvocationSequence;
	private double highestFragility;
	private Invocation lastInvocation = null;
	private List<State> lastState = null;
	private boolean faultHealed = false;
	private Instant faultInjectedTime = null;

	private int episodeIndex = 0;
	private int epochIndex;
	private int waitTimes = 0;
	private int actionExplorationTimes;

	public TestDriver(List<ExtensionalValue> extensionalValues, String modelURI, FeatureValueMonitor monitor) {
		sut = new SUTObject();
		tester = new DiagramUpdater(extensionalValues, monitor);

		tester.init(ExecutionParameters.maxEpisodeNum); // maxEpochNum
		tester.logParameters();
		faultySenarios = new ArrayList<FaultySenario>();
		actionExplorationTimes = 0;
		highestFragility = -1;
		preEpisode(extensionalValues);
	}

	public synchronized void preEpisode(List<ExtensionalValue> extensionalValues) {
		while (!FUMLExecutionEngine.eInstance.isTerminated()) {
			boolean allWaiting = true;

			for (ExtensionalValue value : extensionalValues) {
				if (value instanceof ActiveObject) {
					ActiveObject activeObject = (ActiveObject) value;
					if (activeObject.types.get(0).getClassifierBehavior() != null) {
						ActiveObjectActivation activeObjectActivation = (ActiveObjectActivation) activeObject.objectActivation;
						if (activeObjectActivation.getCurrentState() != ObjectActivationState.WAITING) {
							allWaiting = false;
							break;
						}
					}
				}
			}

			if (!allWaiting) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} //
			else {
				sut.init(extensionalValues);
				tester.episodeStart(sut, faultySenarios);

				
			}
		}

		waitTimes = 0;
		invocationSequence = new ArrayList<Invocation>();
		lastInvocation = null;
		faultHealed = false;
		epochIndex = 0;


		SUTProxy.instance().reset();
	}

	public void recordBestInvocations() {

		StringBuffer str = new StringBuffer();
		str.append("-----------------------------------------------\n");
		str.append("best invocations: \n");
		int i = 0;
		for (Invocation invocation : bestInvocationSequence) {
			str.append(i);
			str.append(": ");
			str.append(invocation.toString());
			str.append("\n ");
			i++;
		}
		str.append("\n higest fragility: ");
		str.append(highestFragility);
		TestLogger.logBestInvocations(str.toString());
	}

	public boolean episodeFinish(double fragility) {
		boolean introducingUncertaintyFinish = false;

		if (fragility == 1) { // fault detected
			actionExplorationTimes = 0;
			highestFragility = -1;
			bestInvocationSequence = null;

		} else {

			if (actionExplorationTimes < ExecutionParameters.maxActionExplorationTimes) {
				if (tester.areAllTransitionsCovered() || episodeIndex > 500) {
					actionExplorationTimes++; // execution for selecting actions
				}
			}  else {
				// reach maximum times, restart
				actionExplorationTimes = 0;
				introducingUncertaintyFinish = true;
			}

			
		}

		

		episodeIndex++;

		return introducingUncertaintyFinish;

	}

	public boolean nextEpoch(double risk) {
		if (areAllObjectsTerminated()) {
			return false;
		}

		if (ExecutionParameters.simulating) {
			triggerNextTransition(risk);
			return true;
		} else {
			if (waitTimes == 0) {
				triggerNextTransition(risk);
				waitTimes = 5000;
				return true;
			} else {
				waitTimes--;
				return false;
			}
		}

	}

	private void triggerNextTransition(double risk) {
		lastState = getActiveStates();
		Invocation invocation = null;
		
			invocation = tester.nextEpoch(sut, risk);
		

		if (invocation == null) {
			System.err.println("do not return call invocation");
			TestLogger.log("--------\n " + "-------do not return call invocation------");
		} else {
			lastInvocation = invocation;
			invocationSequence.add(lastInvocation);
		}


		epochIndex++;
	}

	public boolean isLastInvocationExecuted() {
		if (lastInvocation == null || lastInvocation.transitionActivation.getStatus() == TransitionMetadata.TRAVERSED
				|| lastInvocation.transitionActivation.getTargetActivation().getStatus() == StateMetadata.ACTIVE
				|| lastInvocation.transitionActivation.getSourceActivation()
						.equals(lastInvocation.transitionActivation.getTargetActivation())) {
			return true;
		} else {
			return false;
		}
	}


	public boolean isFaultInjected() {
		if (lastInvocation != null && lastInvocation.isFaultInjection()
				&& lastInvocation.transitionActivation.getStatus() == TransitionMetadata.TRAVERSED) {
			return true;
		} else {
			return false;
		}
	}

	public ChangeEvent getFaultEvent() {

		if (lastInvocation != null && lastInvocation.isFaultInjection()) {
			return lastInvocation.faultEvent;
		} else {
			return null;
		}

	}


	private ChangeEvent getFaultDetectionEvent(Region region) {

		Vertex initial = null;

		for (Vertex vertex : region.getSubvertices()) {
			if (vertex instanceof Pseudostate) {
				Pseudostate ps = (Pseudostate) vertex;
				if (ps.getKind().getValue() == (PseudostateKind.INITIAL)) {
					initial = ps;
				}
			}
		}

		if (initial == null) {
			return null;
		}

		Vertex firstState = initial.getOutgoings().get(0).getTarget();
		return (ChangeEvent) firstState.getOutgoings().get(0).getTriggers().get(0).getEvent();
	}


	
	public boolean areAllObjectsTerminated() {
		return sut.areAllObjectsTerminated();
	}

	public int getActiveObjectNum() {
		return sut.getActiveObjectNum();
	}

	public double[] getStateVariableValues() {
		return sut.getStateVariableValues();
	}

	public double[] getUncertaintyStateVariableValues() {
		double[] res = sut.getUncertaintyStateVariableValues();
		if (res == null) {
			res = new double[sut.getStateVariables().size() + 1];
		}
		return res;
	}


	private List<State> getActiveStates() {

		List<State> activeStates = new ArrayList<State>();
		List<StateMachineExecution> executions = sut.getAllStateMachineExecutions();
		for (StateMachineExecution execution : executions) {
			activeStates.addAll(getActiveStates(execution.getConfiguration().getRoot()));
		}
		return activeStates;
	}

	private List<State> getActiveStates(StateConfiguration configuration) {

		List<State> activeStates = new ArrayList<State>();
		for (StateConfiguration child : configuration.getChildren()) {
			activeStates.addAll(getActiveStates(child));
		}

		if (activeStates.size() > 0) {
			return activeStates;
		}

		SysMLEvolverStateActivation stateActivation = (SysMLEvolverStateActivation) configuration.getVertexActivation();
		if (stateActivation != null) {
			activeStates.add((State) stateActivation.getNode());
		}
		return activeStates;
	}

	public SUTObject getSUT() {
		return sut;
	}

	public synchronized void notifyObjectTerminated(ActiveObjectActivation objectActivation) {

		sut.objectTerminated(objectActivation);
		if (sut.areAllObjectsTerminated()) {
			this.notify();
		}
	}

	public synchronized void notifyObjectIsWaiting(ActiveObjectActivation objectActivation) {

		System.out.println(objectActivation.getName() + " is waiting");

		if (lastInvocation == null) {// 
			System.out.println("last invocation is null!     ~~trigger~~");
			this.notify();
		}

	}

	public synchronized void notifyFaultHandled(ChangeEvent faultEvent) {

		if (!lastInvocation.isFaultInjection()) { // no fault has been injected
			return;
		}

		if (lastInvocation.faultEvent.equals(faultEvent)) {
			faultHealed = true;
		}

	}

	public void waitUntilAllObjectsStable() {
		int stable = 0;
		while (!FUMLExecutionEngine.eInstance.isTerminated()) {
			boolean allStable = sut.areAllObjectsStatble();
			if (!allStable) {
				stable++;
				if (stable == 100) {
					stable = 0;
				}
				synchronized (this) {
					try {
						System.out.println("waitUntilAllObjectsStable..");
						this.wait(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				break;
			}

		}

	}

	public void notifyAllObjectActivations() {
		for (ActiveObjectActivation activation : sut.getActiveObjectActivations().values()) {
			synchronized (activation) {
				activation.notify();
			}
		}
	}

	public boolean isDone() {
		return this.testDone;
	}

	

}
