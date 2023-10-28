package org.eclipse.papyrus.moka.fuml.statemachines.Semantics.CommonBehavior;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.papyrus.moka.async.fuml.Semantics.CommonBehaviors.Communications.AsyncEventPool;
import org.eclipse.papyrus.moka.async.fuml.Semantics.CommonBehaviors.Communications.AsyncObjectActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.ArrivalSignal;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.Communications.EventOccurrence;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.CompletionEventOccurrence;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.DeferredEventOccurrence;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateActivation;
import org.eclipse.uml2.uml.Class;

public class SM_ObjectActivation extends AsyncObjectActivation {

	// Events that have been dispatched but that are actually deferred are placed
	// in this particular event pool. When the state that constrained them to be
	// placed in this pool leaves the state-machine configuration then the deferred
	// events leave this pool and are placed in the regular event pool (to be dispatched
	// again) that is handled by the object activation.
	public AsyncEventPool deferredEventPool;
	
	public SM_ObjectActivation(Class classifier, List<ParameterValue> inputs){
		super(classifier, inputs);
		this.deferredEventPool = new AsyncEventPool(this);
	}
	
	protected CompletionEventOccurrence getNextCompletionEvent(){
		// Return the next completion event available at the pool.
		int i = 0;
		CompletionEventOccurrence completionEvent = null;
		Iterator<EventOccurrence> iterator =  evtPool.iterator();
		
		while(iterator.hasNext()){
			EventOccurrence event = iterator.next();
			if(event instanceof CompletionEventOccurrence){
				completionEvent = (CompletionEventOccurrence)event;
				break;
			}
		}
		evtPool.remove(completionEvent);
		return completionEvent;
	}
	
//	protected int getDeferredEventInsertionIndex(){
//		// Deferred events are always registered after completion events if any.
//		// Return the insertion point for deferred events.
//		int index = 0;
//		int i = 0;
//		while(i < this.eventPool.size()){
//			if(this.eventPool.get(i) instanceof CompletionEventOccurrence){
//				index++;
//			}
//			i++;
//		}
//		return index;
//	}
	
//	public EventOccurrence getNextEvent() {
//		// Completion events are always dispatched first. They are dispatched according
//		// to their order of arrival in the pool. While completion event are available at
//		// the pool no other event is dispatched. If not there is no more completion event
//		// to dispatch then regular events are dispatched according to the currently used
//		// dispatching policy. Note that if the currently dispatched event occurrence was
//		// previously deferred the it is unwrapped and it encapsulated 'deferredEventOccurrence'
//		// is actually dispatched.
//		EventOccurrence nextEvent = this.getNextCompletionEvent(); 
//		if(nextEvent==null){
//			nextEvent = super.getNextEvent();
//			if(nextEvent instanceof DeferredEventOccurrence){
//				nextEvent = ((DeferredEventOccurrence)nextEvent).deferredEventOccurrence;
//			}
//		}
//		return nextEvent;
//	}
	
	public void registerCompletionEvent(StateActivation stateActivation){
		// A completion event is registered in the completion event pool.
		// Completion events are always registered at the head of the event pool.
		// Its final position in the pool depends on the existence of other completion
		// events already registered in the pool.
		CompletionEventOccurrence completionEventOccurrence = new CompletionEventOccurrence();
		completionEventOccurrence.stateActivation = stateActivation;
		int insertionIndex = 0;
//		boolean insertionPointFound = false;
//		while(!insertionPointFound && insertionIndex < this.eventPool.size()){
//			insertionPointFound = !(this.eventPool.get(insertionIndex) instanceof CompletionEventOccurrence);
//			if(!insertionPointFound){
//				insertionIndex++;
//			}
//		}
		this.send(completionEventOccurrence);
		this._send(new ArrivalSignal());
	}
	
	public void registerDeferredEvent(EventOccurrence eventOccurrence, StateActivation deferringState){
		// An event occurrence registered as being deferred is registered within the deferred event pool.
		DeferredEventOccurrence deferredEventOccurrence = new DeferredEventOccurrence();
		deferredEventOccurrence.constrainingStateActivation = deferringState;
		deferredEventOccurrence.deferredEventOccurrence = eventOccurrence;
		this.deferredEventPool.add(deferredEventOccurrence);
	}
	
	public void releaseDeferredEvents(StateActivation deferringState){
		// The release of event occurrence(s) deferred by the deferring state includes the following step:
		// 1. Deferred events are removed from the deferred event pool 
		// 2. Deferred events return to the regular event pool. They are inserted in the pool
		//    after any existing completion event occurrence but before any other events that
		//    arrived later.
		List<DeferredEventOccurrence> releasedEvents = new ArrayList<DeferredEventOccurrence>();
		Iterator<EventOccurrence> iterator = this.deferredEventPool.iterator();
		
		while(iterator.hasNext()){
			DeferredEventOccurrence eventOccurrence = (DeferredEventOccurrence)iterator.next();
			if(eventOccurrence.constrainingStateActivation == deferringState){
				releasedEvents.add(eventOccurrence);
			}
		}
		//int insertionPoint = this.getDeferredEventInsertionIndex();
		int i = 0;
		while(i < releasedEvents.size()){
			// TODO 
			//this.eventPool.add(insertionPoint, releasedEvents.get(i));
			this.send(releasedEvents.get(i));
			this._send(new ArrivalSignal());
			//insertionPoint++;
			i++;
		}
		releasedEvents.clear();
	}
}
