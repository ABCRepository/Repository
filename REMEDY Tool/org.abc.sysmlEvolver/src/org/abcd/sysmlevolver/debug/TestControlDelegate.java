package org.abcd.sysmlevolver.debug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.abc.constraint.ConstraintInstance;
import org.abcd.sysmlevolver.ExecutionEngine;
import org.abcd.sysmlevolver.model.Semantics.SysMLEvolverStateActivation;
import org.abcd.sysmlevolver.model.loci.ActiveObjectActivation;
import org.abcd.sysmlevolver.model.loci.SysMLEvolverExecutor;
import org.abcd.sysmlevolver.test.log.TestLogger;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.papyrus.moka.MokaConstants;
import org.eclipse.papyrus.moka.async.fuml.Semantics.CommonBehaviors.Communications.AsyncObjectActivation;
import org.eclipse.papyrus.moka.async.fuml.Semantics.CommonBehaviors.Communications.AsyncObjectActivation.ObjectActivationState;
import org.eclipse.papyrus.moka.communication.event.isuspendresume.Suspend_Event;
import org.eclipse.papyrus.moka.communication.event.iterminate.Terminate_Event;
import org.eclipse.papyrus.moka.engine.AbstractExecutionEngine;
import org.eclipse.papyrus.moka.fuml.FUMLExecutionEngine;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.CompleteActions.AcceptEventActionEventAccepter;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventAccepter;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.ObjectActivation;
import org.eclipse.papyrus.moka.fuml.debug.FUMLThread;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.debug.SM_ControlDelegate;
import org.eclipse.papyrus.moka.ui.presentation.AnimationUtils;
import org.eclipse.uml2.uml.AcceptEventAction;
import org.eclipse.uml2.uml.ChangeEvent;

public class TestControlDelegate extends SM_ControlDelegate {

	public TestControlDelegate(AbstractExecutionEngine engine) {
		super(engine);
		this.mode = "debug";
	}

	@Override
	public synchronized void registerObjectActivation(ObjectActivation activation, String activationName) {
		super.registerObjectActivation(activation, activationName);
	}

	public void clear() {
		this.objectActivations.clear();
		this.objectActivationsToFUMLThread.clear();
	}



	public synchronized boolean areAllObjectActivationsWaiting(Collection<ActiveObjectActivation> activations) {

		for (ActiveObjectActivation activation : activations) {
			if (activation.operationBeingInvoked()) {
				return false;
			}

			if (activation.getCurrentState() != ObjectActivationState.WAITING) {
				return false;
			}
		}
		return true;

	}

	
	@Override
	public synchronized void notifyWaitingStateEntered(AsyncObjectActivation asyncObjectActivation) {

		FUMLThread thread = this.objectActivationsToFUMLThread.get(asyncObjectActivation);
		if (thread != null) {
			thread.setIsWaiting(true);
			thread.setSuspended(true);
			thread.setStackFrames(new IStackFrame[] {});
			if (MokaConstants.MOKA_AUTOMATIC_ANIMATION && this.mode.equals(ILaunchManager.DEBUG_MODE)) {
				Suspend_Event suspendEvent = new Suspend_Event(thread, DebugEvent.CHANGE, this.getThreads());
				engine.sendEvent(suspendEvent);
				List<AcceptEventAction> waitingAcceptEventActions = new ArrayList<AcceptEventAction>();
				for (EventAccepter eventAccepter : asyncObjectActivation.waitingEventAccepters) {
					if (eventAccepter instanceof AcceptEventActionEventAccepter) {
						AcceptEventAction action = (AcceptEventAction) ((AcceptEventActionEventAccepter) eventAccepter).actionActivation.node;
						waitingAcceptEventActions.add(action);
						AnimationUtils.getInstance().addAnimationMarker(action);
					}
				}
				objectActivationToWaitingAcceptEventActions.put(asyncObjectActivation, waitingAcceptEventActions);
			}
		}

	}

	

	@Override
	public void notifyWaitingStateExit(AsyncObjectActivation asyncObjectActivation,
			AcceptEventActionEventAccepter accepter) {

		super.notifyWaitingStateExit(asyncObjectActivation, accepter);
	}

	@Override
	public synchronized void notifyThreadTermination(ObjectActivation objectActivation) {
		if (this.terminateRequestByClient) {
			return; // do nothing
		}
		if (!FUMLExecutionEngine.eInstance.isTerminated()) {
			FUMLThread fUMLThread = this.objectActivationsToFUMLThread.get(objectActivation);

			Terminate_Event terminateEvent = null;
			if (this.threads.isEmpty()) {
				this.engine.setIsTerminated(true);
				synchronized (this) {
					notifyAll();
				}
				terminateEvent = new Terminate_Event(this.engine.getDebugTarget(), this.getThreads());
			} else if (fUMLThread != null) {
				terminateEvent = new Terminate_Event(fUMLThread, this.getThreads());
			}
			if (terminateEvent != null) {
				FUMLExecutionEngine.eInstance.sendEvent(terminateEvent);
			}
		}

	}

	public synchronized void notifyEnteringTargetStateTimeout(StateActivation targetStateActivation) {


		this.engine.setIsTerminated(true);
		synchronized (this) {
			notifyAll();
		}
	}

	public synchronized void notifyCriticalError(String des) {

		this.engine.setIsTerminated(true);
		synchronized (this) {
			notifyAll();
		}
	}


	public synchronized void notifySafeConditionViolated(ConstraintInstance safeCondition) {

		this.engine.setIsTerminated(true);
		synchronized (this) {
			notifyAll();
		}
	}

	public synchronized void notifyExecutionTerminate() {
		this.engine.setIsTerminated(true);
		synchronized (this) {
			notifyAll();
		}
	}
}
