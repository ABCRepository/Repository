package org.abcd.sysmlevolver.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.abcd.sysmlevolver.model.Semantics.ActiveObject;
import org.abcd.sysmlevolver.model.loci.ActiveObjectActivation;
import org.abcd.sysmlevolver.model.loci.SysMLEvolverExecutor;
import org.abcd.sysmlevolver.test.changeevent.ChangeEventItem;
import org.eclipse.papyrus.moka.fuml.FUMLExecutionEngine;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.ExtensionalValue;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.Execution;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateConfiguration;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateMachineExecution;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.TransitionActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.VertexActivation;
import org.eclipse.uml2.uml.ChangeEvent;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Event;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Trigger;

public class FeatureValueMonitor { 

	private List<Execution> outputOperations; 
	private Set<ChangeEventItem> registeredChangeEvents;

	private SysMLEvolverExecutor executor;

	public FeatureValueMonitor(List<ExtensionalValue> extensionalValues, SysMLEvolverExecutor executor) {
		outputOperations = new ArrayList<Execution>();
		registeredChangeEvents = new HashSet<ChangeEventItem>();
		for (ExtensionalValue value : extensionalValues) {
			if (value instanceof ActiveObject) {
				addOutputOperations((ActiveObject) value);
			}
		}
		this.executor = executor;
	}

	public synchronized void init(List<ExtensionalValue> extensionalValues) {

		outputOperations.clear();
		registeredChangeEvents.clear();

		for (ExtensionalValue value : extensionalValues) {
			if (value instanceof ActiveObject) {
				addOutputOperations((ActiveObject) value);
			}
		}
	}

	private void addOutputOperations(ActiveObject object) {

		for (Class type : object.types) {
			for (Operation operation : type.getOperations()) {
				if (operation.getAppliedStereotype("testing::SyncOperation") != null) {
					outputOperations.add(object.operationExecutions.get(operation.getName()));
				}
			}
		}

	}

	public boolean update() {
		assert (outputOperations.size() != 0);
		for (Execution outputAction : outputOperations) {
			outputAction.execute();
			if (FUMLExecutionEngine.eInstance.isTerminated()) {
				return false;
			}
		}
		return true;
	}

	public void run() {
		while (executor == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		while (!FUMLExecutionEngine.eInstance.isTerminated()) {
			boolean needWait = false;
			synchronized (executor) {
				for (Execution outputAction : outputOperations) {
					outputAction.execute();
					if (FUMLExecutionEngine.eInstance.isTerminated()) {
						break;
					}
				}
			}

			int sleepTime = 10;

			if (needWait) {
				sleepTime = 5000;
			} else {
				evaluateChangeEvents();
			}

			// TODO here should wait until next cycle has completed

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	public synchronized boolean sendChangeEvent(ChangeEvent event) {
		Set<ChangeEventItem> notTriggeredChangeEvents = new HashSet<ChangeEventItem>();
		boolean changeEventGenerated = false;
		for (ChangeEventItem item : registeredChangeEvents) {
			if (item.equals(event)) {
				item.sendChangeEvent();
				changeEventGenerated = true;
			} else {
				notTriggeredChangeEvents.add(item);
			}
		}
		this.registeredChangeEvents = notTriggeredChangeEvents;
		return changeEventGenerated;

	}

	public synchronized boolean evaluateChangeEvents() {
		Set<ChangeEventItem> notTriggeredChangeEvents = new HashSet<ChangeEventItem>();
		boolean changeEventGenerated = false;
		for (ChangeEventItem item : registeredChangeEvents) {
			if (item.evaluate()) {
				item.sendChangeEvent();
				System.out.println("send change event:" + item.toString());
				changeEventGenerated = true;
			} else {
				notTriggeredChangeEvents.add(item);
			}
		}
		this.registeredChangeEvents = notTriggeredChangeEvents;

		return changeEventGenerated;
	}

	public synchronized void registerChangeEvent(ActiveObjectActivation objectActivation) {
		long time1 = System.currentTimeMillis();
		StateMachineExecution execution = (StateMachineExecution) (objectActivation.classifierBehaviorInvocations
				.get(0).execution);
		registerChangeEvent(objectActivation, execution.getConfiguration().getRoot());
		long time2 = System.currentTimeMillis();
	}

	private synchronized void registerChangeEvent(ActiveObjectActivation objectActivation,
			StateConfiguration configuration) {
		for (StateConfiguration child : configuration.getChildren()) {
			registerChangeEvent(objectActivation, child);
		}
		VertexActivation vertexActivation = configuration.getVertexActivation();
		if (vertexActivation == null) {
			return;
		}
		for (TransitionActivation transition : vertexActivation.getOutgoingTransitions()) {
			List<Trigger> triggers = ((Transition) transition.getNode()).getTriggers();
			for (Trigger trigger : triggers) {
				Event event = trigger.getEvent();
				if (event instanceof ChangeEvent) {
					ChangeEventItem item = new ChangeEventItem(objectActivation,
							((ChangeEvent) event).getChangeExpression());
					addChangeEventItem(item);
					// }
				}
			}
		}
	}

	public List<TransitionActivation> getTransitionByLength(VertexActivation vertexActivation, int length) {
		List<TransitionActivation> finalTransitionActivations = new ArrayList<TransitionActivation>();
		List<TransitionActivation> transitionActivations = vertexActivation.getOutgoingTransitions();
		if (transitionActivations.size() <= length)
			return transitionActivations;
		else {
			for (int i = 0; i < length; i++) {
				Random random = new Random();
				finalTransitionActivations.add(transitionActivations.get(random.nextInt(transitionActivations.size())));
			}
		}
		return finalTransitionActivations;
	}

	private void addChangeEventItem(ChangeEventItem item) {
		for (ChangeEventItem registeredItem : registeredChangeEvents) {
			if (registeredItem.equals(item)) {
				return;
			}
		}
		registeredChangeEvents.add(item);
	}

}
