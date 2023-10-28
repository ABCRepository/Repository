/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Jeremie Tatibouet (CEA LIST)
 *
 *****************************************************************************/
package org.eclipse.papyrus.moka.fuml.statemachines.debug;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrus.moka.MokaConstants;
import org.eclipse.papyrus.moka.async.fuml.debug.AsyncControlDelegate;
import org.eclipse.papyrus.moka.communication.event.isuspendresume.Suspend_Event;
import org.eclipse.papyrus.moka.debug.MokaStackFrame;
import org.eclipse.papyrus.moka.engine.AbstractExecutionEngine;
import org.eclipse.papyrus.moka.fuml.Semantics.Activities.IntermediateActivities.ActivityEdgeInstance;
import org.eclipse.papyrus.moka.fuml.Semantics.Activities.IntermediateActivities.ActivityNodeActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Object_;
import org.eclipse.papyrus.moka.fuml.debug.FUMLThread;
import org.eclipse.papyrus.moka.fuml.presentation.FUMLPresentationUtils;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateMachineSemanticVisitor;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.TransitionActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.VertexActivation;
import org.eclipse.papyrus.moka.ui.presentation.AnimationUtils;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Vertex;

public class SM_ControlDelegate extends AsyncControlDelegate {

	public SM_ControlDelegate(AbstractExecutionEngine engine) {
		super(engine);
	}

	public boolean control(Object object) {
		if (this.engine.isTerminated()) {
			return false ;
		}

		// Retrieves the semantic element
		EObject semanticElement = null ;
		Object_ executionContext = null ;
		if (object instanceof ActivityNodeActivation) {
			semanticElement = ((ActivityNodeActivation)object).node ;
			if (((ActivityNodeActivation)object).group != null)
				executionContext = ((ActivityNodeActivation)object).getExecutionContext() ;
		}
		else if (object instanceof ActivityEdgeInstance) {
			semanticElement = ((ActivityEdgeInstance)object).edge ;
			if (((ActivityEdgeInstance)object).group != null)
				executionContext = ((ActivityEdgeInstance)object).group.getActivityExecution().context ;
		}else if(object instanceof StateMachineSemanticVisitor){
			semanticElement = ((StateMachineSemanticVisitor)object).getNode();
			executionContext = ((StateMachineSemanticVisitor)object).getExecutionContext();
		}
		else {
			System.err.println(new Exception("Unexpected element in ControlDelegate::control")) ;
			this.engine.setIsTerminated(true) ;
			return false ;
		}
		if (executionContext != null) {
			try {
				FUMLThread thread = objectActivationsToFUMLThread.get(executionContext.objectActivation);//(FUMLThread) this.getThreads()[0];
				if(thread == null){
					return false;
				}
				if (semanticElement != null 
						&& MokaConstants.MOKA_AUTOMATIC_ANIMATION 
						&& this.mode.equals(ILaunchManager.DEBUG_MODE)
						&& !thread.isStepping()) {
					this.animate(semanticElement) ;
				}
				int reasonForSuspending = -1 ;
				if (thread.getReasonForSuspending() != -1) {
					reasonForSuspending = thread.getReasonForSuspending() ;
				}
				else if (thread.getReasonForResuming() != DebugEvent.CLIENT_REQUEST) {
					reasonForSuspending = DebugEvent.STEP_END ;
				}
				else if (this.elementsWithBreakpoints.contains(semanticElement)) { // Tries to check if a breakpoint applies
					reasonForSuspending = DebugEvent.BREAKPOINT ;
				}
				if (reasonForSuspending != -1) {
					thread.setSuspended(true) ;
					thread.setReasonForSuspending(reasonForSuspending) ;
					MokaStackFrame stackFrame = FUMLPresentationUtils.getMokaStackFrame(object) ;
					stackFrame.setThread(thread) ;
					thread.setStackFrames(new IStackFrame[]{stackFrame}) ;
					Suspend_Event suspendEvent = new Suspend_Event(thread, reasonForSuspending, this.getThreads()) ;
					engine.sendEvent(suspendEvent) ;
					String lock = new String() ;
					synchronized (lock) {
						lock.wait() ;
					}
				}
			} catch (InterruptedException e) {
				System.err.println(e) ;
			}
		}

		return !this.engine.isTerminated() ;
	}

	protected boolean diagramsExistFor(Element element) {
		Behavior behavior = null ;
		Element tmp = element ;
		while (behavior == null) {
			tmp = tmp.getOwner() ;
			if (tmp instanceof Behavior) {
				behavior = (Behavior)tmp ;
			}
		}
		if (behavior != null && !AnimationUtils.getInstance().diagramsExistFor(behavior))
			return false ;
		return AnimationUtils.getInstance().diagramsExistFor(element) ;

	}

	public void animate(EObject element) {
		if(element instanceof Vertex || element instanceof Transition){
			AnimationUtils.getInstance().addAnimationMarker(element) ;
		}else{
			//FIXME: should delegate to super class implementation
		}
	}

	public void inactive(Object object){
		
		EObject element = null;
		if(object instanceof VertexActivation){
			element = ((VertexActivation)object).getNode();
		}
		else if(object instanceof TransitionActivation){
			element = ((TransitionActivation)object).getNode();
		}
		
		if(element != null){
//			try {
//			Thread.sleep(MokaConstants.MOKA_ANIMATION_DELAY);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
			AnimationUtils.getInstance().removeAnimationMarker(element) ;
		}
		
	}
}
