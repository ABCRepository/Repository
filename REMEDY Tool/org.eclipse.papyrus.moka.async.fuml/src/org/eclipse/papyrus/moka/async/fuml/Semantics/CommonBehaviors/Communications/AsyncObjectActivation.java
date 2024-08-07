/*****************************************************************************
 * Copyright (c) 2014 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  CEA LIST - Initial API and implementation
 *  Jeremie Tatibouet (CEA LIST) - Alignment of the asynchronous implementation of fUML with the version 1.2 of the standard
 *  
 *****************************************************************************/
package org.eclipse.papyrus.moka.async.fuml.Semantics.CommonBehaviors.Communications;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.papyrus.infra.core.Activator;
import org.eclipse.papyrus.moka.MokaConstants;
import org.eclipse.papyrus.moka.async.fuml.debug.AsyncControlDelegate;
import org.eclipse.papyrus.moka.async.fuml.debug.AsyncDebug;
import org.eclipse.papyrus.moka.fuml.FUMLExecutionEngine;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.CompleteActions.AcceptEventActionEventAccepter;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.ArrivalSignal;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.ClassifierBehaviorInvocationEventAccepter;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventAccepter;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.InvocationEventOccurrence;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.ObjectActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.SignalEventOccurrence;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.SignalInstance;
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.ChoiceStrategy;
import org.eclipse.papyrus.moka.fuml.standardlibrary.library.io.StandardOutputChannelImpl;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Class;

/**
 * Extend the original ObjectActivation class in order to support starting of execution
 * of the different classifiers behaviors over a new Java Thread.
 */
public class AsyncObjectActivation extends ObjectActivation implements Runnable {

	/* Thread State */
	/**
	 * The Enum ObjectActivationState.
	 */
	public enum ObjectActivationState {

		/** The running. */
		RUNNING,
		/** The stopped. */
		STOPPED,
		/** The waiting. */
		WAITING
	} // WAITING State added for connection with debug api

	/** The current state. */
	protected ObjectActivationState currentState = null;

	/* Arguments of the ObjectActivation */
	/** The classifier. */
	final protected Class classifier;

	/** The inputs. */
	final protected List<ParameterValue> inputs;

	/* The event pool handled by the ObjectActivation */
	/** The evt pool. */
	public AsyncEventPool evtPool;

	/**
	 * Constructor of AsyncObjectActivation.
	 *
	 * @param classifier
	 *            that need to be executed on the current object activation
	 * @param inputs
	 *            parameters that are provided to the execution
	 */
	public AsyncObjectActivation(Class classifier, List<ParameterValue> inputs) {
		super();
		this.classifier = classifier;
		this.inputs = inputs;
		this.evtPool = new AsyncEventPool(this);
	}
	
	public void send(EventOccurrence eventOccurrence){
		evtPool.send(eventOccurrence);
	}
	
	@Override
	public void _send(ArrivalSignal signal) {
//		// Signal the arrival of a new signal instance in the event pool.
//		// *** This should send an ArrivalSignal to the EventDispatchLoop. ***
//
//		this.signalCount = this.signalCount + 1;
//		if (this.signalCount == 1) {
//			this._startObjectBehavior();
//		}
	} // _send
	
	/**
	 * Implementation of the behavior of the current object activation.
	 */
	public void run() {
		/* 1. The current object activation is in the running state */
		this.currentState = ObjectActivationState.RUNNING;
		/* 2. Execute behavior(s) associated to the given classifier */
		try {
			this.startBehavior(this.classifier, this.inputs);
		} catch (Exception e) {
			Activator.log.error(e);
			if (!MokaConstants.SILENT_MODE) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Moka", "An unexpected error occurred during execution. See error log for details.");
					}
				});
			}
			((AsyncControlDelegate) FUMLExecutionEngine.eInstance.getControlDelegate()).notifyThreadTermination(this); // Added for connection with debug api
		}
		
		/* 3. While current object activation is running then dispatch events */
		while (!FUMLExecutionEngine.eInstance.isTerminated() && this.currentState.equals(ObjectActivationState.RUNNING)) {
			try {
				this.dispatchNextEvent(); /* Dispatch is blocking if no SignalInstance available */
			} catch (Exception e) {
				Activator.log.error(e);
				if (!MokaConstants.SILENT_MODE) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(Display.getDefault().getActiveShell(), "Moka", "An unexpected error occurred during execution. See error log for details.");
						}
					});
				}
				((AsyncControlDelegate) FUMLExecutionEngine.eInstance.getControlDelegate()).notifyThreadTermination(this); // Added for connection with debug api
			}
			if (this.waitingEventAccepters.isEmpty()) {
				this.currentState = ObjectActivationState.STOPPED;
			}
		}
		((AsyncControlDelegate) FUMLExecutionEngine.eInstance.getControlDelegate()).notifyThreadTermination(this); // Added for connection with debug api
	}

	// Added for connection with debug API
	/**
	 * Gets the current state.
	 *
	 * @return the current state
	 */
	public ObjectActivationState getCurrentState() {
		return this.currentState;
	}

	/**
	 * Causality is broken here in order to let the thread that emitted
	 * the signal to continue its execution.
	 *
	 * @param signalInstance
	 *            the signal instance
	 */
	@Override
	public synchronized void send(SignalInstance signalInstance) {
		SignalEventOccurrence eventOccurrence = new SignalEventOccurrence();
		eventOccurrence.signalInstance = (SignalInstance) signalInstance.copy();
		this.evtPool.send(eventOccurrence);
	}

	// Added for connection with debug API
	/** The has been waiting. */
	protected boolean hasBeenWaiting = false;

	/**
	 * Retrieve a SignalInstance stored in the event pool.
	 *
	 * @return the next event
	 */
	@Override
	public EventOccurrence getNextEvent() {
		// Added for connection with debug API
		if (this.evtPool.isEmpty()) {
			this.currentState = ObjectActivationState.WAITING;
			this.hasBeenWaiting = true;
			
			((AsyncControlDelegate) FUMLExecutionEngine.eInstance.getControlDelegate()).notifyWaitingStateEntered(this);
		}
		System.err.println("running........... async");
		EventOccurrence eventOccurrence = this.evtPool.getNextEvent();
		this.currentState = ObjectActivationState.RUNNING;

		if (eventOccurrence != null) {
			if(eventOccurrence instanceof SignalEventOccurrence){
				AsyncDebug.println("[EVENT CONSUMED] occurrence for a signal:  " + ((SignalEventOccurrence)eventOccurrence).signalInstance);
			}
			if(eventOccurrence instanceof InvocationEventOccurrence){
				AsyncDebug.println("[EVENT CONSUMED] occurrence to start a classifier behavior: " + ((InvocationEventOccurrence)eventOccurrence).execution);
			}
		}
		return eventOccurrence;
	}

	/**
	 * Start the event dispatch loop for this object activation (if it has
	 * not already been started).
	 * If a classifier is given that is a type of the object of this object
	 * activation and there is not already a classifier behavior execution
	 * for it, then create a classifier behavior execution for it.
	 * Otherwise, create a classifier behavior execution for each of the
	 * types of the object of this object activation which has a classifier
	 * behavior or which is a behavior itself
	 * and for which there is not currently a classifier behavior execution.
	 * Start EventDispatchLoop
	 *
	 * @param classifier
	 *            the classifier
	 * @param inputs
	 *            the inputs
	 */
	@Override
	public void startBehavior(Class classifier, List<ParameterValue> inputs) {
		/* 1. Start behavior of the current classifier */
		if (classifier == null) {
			AsyncDebug.println("Starting behavior for all classifiers...");
			// *** Start all classifier behaviors concurrently. ***
			List<Class> types = this.object.types;
			for (Iterator<Class> i = types.iterator(); i.hasNext();) {
				Class type = i.next();
				if (type instanceof Behavior | type.getClassifierBehavior() != null) {
					this.startBehavior(type, new ArrayList<ParameterValue>());
				}
			}
		} else {
			AsyncDebug.println("Starting behavior for " + classifier.getName() + "...");
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
				this.evtPool.send(eventOccurrence);
			}
		}

	}

	/**
	 * Stop all the classifier behaviors. The current object activation enters the STOPPED state
	 */
	@Override
	public void stop() {
		/* 1. Terminate all of my classifier behaviors */
		super.stop();
		/* 2. Terminate my execution thread */
		this.currentState = ObjectActivationState.STOPPED;
		
		this.evtPool.send(new EventOccurrence());
	}

	/** The out. */
	final static protected IOConsoleOutputStream out = StandardOutputChannelImpl.getConsole().newOutputStream();;

	/**
	 * Get the next signal instance out of the event pool.
	 * If there is one or more waiting event accepters with triggers that
	 * match the signal instance, then dispatch it to exactly one of those
	 * waiting accepters
	 */
	@Override
	public void dispatchNextEvent() {
		
		/* 1. Get next event is blocking if used on a empty event pool */
		EventOccurrence eventOccurrence = this.getNextEvent();
		
		AsyncDebug.println("[dispatchNextEvent] eventOccurrence = " + eventOccurrence);
		/* 2. Look for EventAccepter that match the selected SignalInstance */
		List<Integer> matchingEventAccepterIndexes = new ArrayList<Integer>();
		List<EventAccepter> waitingEventAccepters = this.waitingEventAccepters;
		for (int i = 0; i < waitingEventAccepters.size(); i++) {
			EventAccepter eventAccepter = waitingEventAccepters.get(i);
			if (eventAccepter.match(eventOccurrence)) {
				matchingEventAccepterIndexes.add(i);
			}
		}
		/* 3. Choose one matching event accepter non-deterministically */
		if (matchingEventAccepterIndexes.size() > 0) {
			int j = ((ChoiceStrategy) this.object.locus.factory.getStrategy("choice")).choose(matchingEventAccepterIndexes.size());
			EventAccepter selectedEventAccepter = this.waitingEventAccepters.get(matchingEventAccepterIndexes.get(j - 1));
			// this.waitingEventAccepters.remove(j - 1);
			this.waitingEventAccepters.remove(selectedEventAccepter);
			if (this.hasBeenWaiting) {
				this.hasBeenWaiting = false;
				if (selectedEventAccepter instanceof AcceptEventActionEventAccepter) {
					((AsyncControlDelegate) FUMLExecutionEngine.eInstance.getControlDelegate()).notifyWaitingStateExit(this, (AcceptEventActionEventAccepter) selectedEventAccepter);
				}
			}
			selectedEventAccepter.accept(eventOccurrence);
			
		}else{
			AsyncDebug.printLostSignal(eventOccurrence, this, out);
		}
	}	
}
