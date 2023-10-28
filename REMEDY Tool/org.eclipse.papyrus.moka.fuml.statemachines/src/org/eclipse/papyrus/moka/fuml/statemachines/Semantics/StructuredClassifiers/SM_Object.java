package org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StructuredClassifiers;

import java.util.Iterator;
import java.util.List;

import org.eclipse.papyrus.moka.async.fuml.Semantics.Classes.Kernel.AsyncObject_;
import org.eclipse.papyrus.moka.async.fuml.Semantics.CommonBehaviors.Communications.AsyncObjectActivation;
import org.eclipse.papyrus.moka.async.fuml.debug.AsyncControlDelegate;
import org.eclipse.papyrus.moka.async.fuml.debug.AsyncDebug;
import org.eclipse.papyrus.moka.fuml.FUMLExecutionEngine;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.CommonBehavior.SM_ObjectActivation;
import org.eclipse.uml2.uml.Class;

public class SM_Object extends AsyncObject_ {
	
	public void startBehavior(Class classifier, List<ParameterValue> inputs) {
		// The behavior captured here is almost identical to the one provide by Object_.
		// Instead of using a simple ObjectActivation we use a StateMachineObjectActivation.
		// This specialized kind of ObjectActivation allows the registering of completion events.
		if (this.objectActivation == null) {
			this.objectActivation = new SM_ObjectActivation(classifier, inputs);
			this.objectActivation.object = this;
		}
		/* 1. Create Thread Name */
		String threadName = "Thread_";
		Iterator<Class> classes = this.types.iterator();
		while (classes.hasNext()) {
			threadName += classes.next().getName();
			if (classes.hasNext()) {
				threadName += "|";
			}
		}
		/* 2. Pattern to start the Runnable corresponding to the AsyncObjectActivation */
		// Thread objectactivationThread = new Thread((AsyncObjectActivation)this.objectActivation, threadName);
		objectactivationThread = new Thread((AsyncObjectActivation) this.objectActivation, threadName);

		((AsyncControlDelegate) FUMLExecutionEngine.eInstance.getControlDelegate()).registerObjectActivation(this.objectActivation, threadName); // Added for connection with debug api

		objectactivationThread.start();

		AsyncDebug.println("[NEW THREAD] Active object instance started on a new thread");
	}
	
	@Override
	public void destroy() {
		// In addition to realize the normal process of stopping the object activation
		// as well as removing the current object from the locus, this destruction phase
		// also implies removal of all events remaining in the pool. This prevents the
		// dispatch loop to actually get the next event (even if at this step there is no
		// chance to match an accepter) whereas the current object is not anymore registered.
		// in the Locus.
		if(this.objectActivation!=null){
			((AsyncObjectActivation)objectActivation).evtPool.clear();
		}
		super.destroy();
	}

}
