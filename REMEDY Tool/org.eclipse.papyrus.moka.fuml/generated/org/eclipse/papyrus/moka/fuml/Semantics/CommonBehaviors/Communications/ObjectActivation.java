/*****************************************************************************
 * Copyright (c) 2012 CEA LIST.
 *
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  CEA LIST - Initial API and implementation
 *  Jeremie Tatibouet (CEA LIST)
 *
 *****************************************************************************/
package org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Object_;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.ChoiceStrategy;
import org.eclipse.papyrus.moka.fuml.debug.Debug;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Class;

public class ObjectActivation {

	/*
	 * The invocations of the executing classifier behaviors for this object activation
	 * 
	 * fUML12-35 Initial execution of an activity is not run to completion
	 */
	public List<ClassifierBehaviorInvocationEventAccepter> classifierBehaviorInvocations = new ArrayList<ClassifierBehaviorInvocationEventAccepter>();

	/*
	 * The set of event accepters waiting for event occurrences to be dispatched from the pool
	 */
	public List<EventAccepter> waitingEventAccepters = new ArrayList<EventAccepter>();

	/*
	 * The pool of event occurrences received by the object of this object activation, pending
	 * dispatching as events.
	 * 
	 * fUML12-35 Initial execution of an activity is not run to completion
	 */
	public List<EventOccurrence> eventPool = new ArrayList<EventOccurrence>();

	/*
	 * The object whose active behavior is being handled by this active object.
	 */
	public Object_ object;

	public void stop() {
		// Stop this object activation by terminating all classifier behavior
		// executions.
		
		//fUML12-35 Initial execution of an activity is not run to completion
		
		List<ClassifierBehaviorInvocationEventAccepter> classifierBehaviorExecutions = this.classifierBehaviorInvocations;
		for (int i = 0; i < classifierBehaviorExecutions.size(); i++) {
			ClassifierBehaviorInvocationEventAccepter classifierBehaviorExecution = classifierBehaviorExecutions.get(i);
			classifierBehaviorExecution.terminate();
		}
	}

	public void register(EventAccepter accepter) {
		// Register the given event accepter to wait for a dispatched signal
		// event.
		Debug.println("[register] object = " + this.object);
		Debug.println("[register] accepter = " + accepter);
		this.waitingEventAccepters.add(accepter);
	}

	public void unregister(EventAccepter accepter) {
		// Remove the given event accepter for the list of waiting event
		// accepters.
		Debug.println("[unregister] object = " + this.object);
		Debug.println("[unregister] accepter = " + accepter);
		boolean notFound = true;
		int i = 1;
		while (notFound & i <= this.waitingEventAccepters.size()) {
			if (this.waitingEventAccepters.get(i - 1) == accepter) {
				this.waitingEventAccepters.remove(i - 1);
				notFound = false;
			}
			i = i + 1;
		}
	}

	public void dispatchNextEvent() {
		//fUML12-35 Initial execution of an activity is not run to completion
		if (this.eventPool.size() > 0) {
			EventOccurrence eventOccurrence = this.getNextEvent();
			Debug.println("[dispatchNextEvent] eventOccurrence = " + eventOccurrence);
			List<Integer> matchingEventAccepterIndexes = new ArrayList<Integer>();
			List<EventAccepter> waitingEventAccepters = this.waitingEventAccepters;
			for (int i = 0; i < waitingEventAccepters.size(); i++) {
				EventAccepter eventAccepter = waitingEventAccepters.get(i);
				if (eventAccepter.match(eventOccurrence)) {
					matchingEventAccepterIndexes.add(i);
				}
			}
			if (matchingEventAccepterIndexes.size() > 0) {
				// *** Choose one matching event accepter non-deterministically.
				// ***
				int j = ((ChoiceStrategy) this.object.locus.factory.getStrategy("choice")).choose(matchingEventAccepterIndexes.size());
				EventAccepter selectedEventAccepter = this.waitingEventAccepters.get(matchingEventAccepterIndexes.get(j - 1));
				int removeAt = matchingEventAccepterIndexes.get(j - 1);
				this.waitingEventAccepters.remove(removeAt);
				selectedEventAccepter.accept(eventOccurrence);
			}
		}
	}

	public EventOccurrence getNextEvent() {
		// Get the next event from the event pool, using a get next event
		// strategy.
		
		//fUML12-35 Initial execution of an activity is not run to completion
		
		return ((GetNextEventStrategy) this.object.locus.factory.getStrategy("getNextEvent")).getNextEvent(this);
	}

	public void send(SignalInstance signalInstance) {
		// Add a signal event occurrence for the given signal instance to the event pool
		// and signal that a new event occurrence has arrived.
		
		//fUML12-35 Initial execution of an activity is not run to completion
		
		SignalEventOccurrence eventOccurrence = new SignalEventOccurrence();
		eventOccurrence.signalInstance = (SignalInstance) signalInstance.copy();
		this.eventPool.add(eventOccurrence);
		_send(new ArrivalSignal());
	}

	public void startBehavior(Class classifier, List<ParameterValue> inputs) {
		// Start the event dispatch loop for this object activation (if it has
		// not already been started).
		// If a classifier is given that is a type of the object of this object
		// activation and there is not already a classifier behavior invocation
		// for it,
		// then create a classifier behavior invocation for it.
		// Otherwise, create a classifier behavior invocation for each of the
		// types of the object of this object activation which has a classifier
		// behavior or which is a behavior itself
		// and for which there is not currently a classifier behavior invocation.
		// Start EventDispatchLoop
		
		//fUML12-35 Initial execution of an activity is not run to completion
		
		_startObjectBehavior();
		if (classifier == null) {
			Debug.println("[startBehavior] Starting behavior for all classifiers...");
			// *** Start all classifier behaviors concurrently. ***
			List<Class> types = this.object.types;
			for (Iterator<Class> i = types.iterator(); i.hasNext();) {
				Class type = i.next();
				if (type instanceof Behavior | type.getClassifierBehavior() != null) {
					this.startBehavior(type, new ArrayList<ParameterValue>());
				}
			}
		} else {
			Debug.println("[startBehavior] Starting behavior for " + classifier.getName() + "...");
			boolean notYetStarted = true;
			int i = 1;
			while (notYetStarted & i <= this.classifierBehaviorInvocations.size()) {
				notYetStarted = (this.classifierBehaviorInvocations.get(i - 1).classifier != classifier);
				i = i + 1;
			}
			if (notYetStarted) {
				/*
				 * 1. Register an event accepter to denote the waiting of an InvocationEventoccurence that allows the classifier behavior to start
				 * 2. Place in the event pool an InvocationEventOccurrence. When consumed it will triggers the execution of the classifier behavior in an RTC step
				 * 3. Force the starting of the dispatch loop using the usual pattern of the ArrivalSignal
				 */
				ClassifierBehaviorInvocationEventAccepter newInvocation = new ClassifierBehaviorInvocationEventAccepter();
				newInvocation.objectActivation = this;
				this.classifierBehaviorInvocations.add(newInvocation);
				newInvocation.invokeBehavior(classifier, inputs);
				InvocationEventOccurrence eventOccurrence = new InvocationEventOccurrence();
				eventOccurrence.execution = newInvocation.execution;
				this.eventPool.add(eventOccurrence);
				_send(new ArrivalSignal());
			}
		}
	}


	int signalCount = 0;

	public void _startObjectBehavior() {
		// *** This should start the EventDispatchLoop ***

		while (this.signalCount > 0) {
			this.dispatchNextEvent();
			signalCount = signalCount - 1;
		}
	} // _startObjectBehavior

	public void _send(ArrivalSignal signal) {
		// Signal the arrival of a new signal instance in the event pool.
		// *** This should send an ArrivalSignal to the EventDispatchLoop. ***

		this.signalCount = this.signalCount + 1;
		if (this.signalCount == 1) {
			this._startObjectBehavior();
		}
	} // _send

}
