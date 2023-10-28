package org.abcd.sysmlevolver.test.sut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.abcd.sysmlevolver.model.Semantics.ActiveObject;
import org.abcd.sysmlevolver.model.loci.ActiveObjectActivation;
import org.abcd.sysmlevolver.test.utils.FaultySenario;
import org.abcd.sysmlevolver.test.utils.PropertyUtil;
import org.eclipse.papyrus.moka.async.fuml.Semantics.CommonBehaviors.Communications.AsyncObjectActivation.ObjectActivationState;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.ExtensionalValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.FeatureValue;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.RegionActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateMachineExecution;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.TransitionActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.VertexActivation;
import org.eclipse.uml2.uml.CallEvent;
import org.eclipse.uml2.uml.ChangeEvent;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Event;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Stereotype;

public class SUTObject {
	
	
	public static String name = "";
	
	private int activeObjectNum = -1;
	
	private Map<String, PropertyUtil> stateVariables;
	private Map<String, PropertyUtil> attributeVariables;
	private Map<String, ActiveObjectActivation> activeObjectActivations;
	
	public double[] uncertaintyStateVariableValues;
	
	public double uncertaintiesDis;
	public double uncertaintiesIndex;
	
	private Map<NamedElement, TransitionActivation> transitionActivations;
	
	public synchronized void init(List<ExtensionalValue> extensionalValues){
		activeObjectNum = 0;
		activeObjectActivations = new HashMap<String, ActiveObjectActivation>();
		for (ExtensionalValue value : extensionalValues) {
			if (value instanceof ActiveObject) {
				ActiveObject activeObject = (ActiveObject) value;
				if (activeObject.types.get(0).getClassifierBehavior() != null) {
					activeObjectNum++;
					ActiveObjectActivation activeObjectActivation = (ActiveObjectActivation) activeObject.objectActivation;
					activeObjectActivations.put(activeObjectActivation.getName(), activeObjectActivation);
				}
			}
		}
		
		initStateVariables();
		
		uncertaintiesDis = 0;
		uncertaintiesIndex = 0;
		
		transitionActivations = this.getAllTransitionActivations();
		
		uncertaintyStateVariableValues = null;
	}
	
	public synchronized void objectTerminated(ActiveObjectActivation objectActivation) {
		
		ActiveObjectActivation terminatedObject = activeObjectActivations.remove(objectActivation.getName());
		
		if (terminatedObject != null) {
			activeObjectNum--;
		} else {
			System.err.println(objectActivation.getName() + " is not contained in test driver");
		}
		
	}
	
	public int getActiveObjectNum() {
		return activeObjectNum;
	}
	
	public String getStateVariableNames() {
		return stateVariables.keySet().toString();
	}
	
	public Map<String, PropertyUtil> getAttributeVariables() {
		return attributeVariables;
	}
	
	public Map<String, PropertyUtil> getStateVariables() {
		return stateVariables;
	}
	
	public String getStateVAriableValuesStr() {
		
		StringBuffer res = new StringBuffer();
		for(Entry<String, PropertyUtil> entry : stateVariables.entrySet()){
			res.append(entry.getValue().getValue());
			res.append(", ");
		}
		res.delete(res.length() - 2, res.length());
		return res.toString();
		
	}
	
	public double[] getUncertaintyStateVariableValues() {
		return uncertaintyStateVariableValues;
	}
	
	public double[] getStateVariableValues() {
		
		double[] state = new double[stateVariables.size()];
		int i = 0;
		for(PropertyUtil p : stateVariables.values()){
			state[i] = p.getValue();
			i++;
		}
		return state;
	}
	
	public synchronized boolean areAllObjectsWaiting() {
		
		for(ActiveObjectActivation activation : activeObjectActivations.values()){
			if(activation.operationBeingInvoked() || activation.getCurrentState() != ObjectActivationState.WAITING){
				return false;
			}
		}
		return true;
	}
	
	public synchronized boolean areAllObjectsStatble() {

		if (activeObjectActivations == null || activeObjectActivations.isEmpty()) {
			return true;
		}

		for (ActiveObjectActivation activation : activeObjectActivations.values()) {
			if (activation.operationBeingInvoked() || (activation.getCurrentState() != ObjectActivationState.WAITING
					&& (!activation.isTraversing()))) {
				return false;
			}
		}
		return true;
	}
	
	
	public synchronized List<StateMachineExecution> getAllStateMachineExecutions() {
		
		ArrayList<StateMachineExecution> executions = new ArrayList<StateMachineExecution>();
		
		for (ActiveObjectActivation objectActivation : activeObjectActivations.values()) {
			StateMachineExecution execution = (StateMachineExecution) (objectActivation.classifierBehaviorInvocations
					.get(0).execution);
			executions.add(execution);
		}
		return executions;
	}
	
	public synchronized List<Class> getAllClasses() {
		
		List<Class> res = new ArrayList<Class>();
		for(ActiveObjectActivation activeObjectActivation : activeObjectActivations.values()) {
			Class c = activeObjectActivation.object.types.get(0);
			res.add(c);
		}
		return res;
		
	}
	
	
	
	
	
	public synchronized Map<String, ActiveObjectActivation> getActiveObjectActivations() {
		
		Map<String, ActiveObjectActivation> res = new HashMap<String, ActiveObjectActivation>();
		
		for(String key : activeObjectActivations.keySet()) {
			res.put(key, activeObjectActivations.get(key));
		}
		
		return res;
	}
	
	public boolean areAllObjectsTerminated() {
		return activeObjectNum == 0;
	}
	
	private void initStateVariables() {
		
		if(stateVariables == null){
			stateVariables = new LinkedHashMap<String, PropertyUtil>();
			attributeVariables = new LinkedHashMap<String, PropertyUtil>();
			for (ActiveObjectActivation objectActivation : activeObjectActivations.values()) {
				for (FeatureValue featureValue : objectActivation.object.featureValues) {
					PropertyUtil p = new PropertyUtil(featureValue);
					if (featureValue.feature.getAppliedStereotype("testing::StateVariable") != null) {
						stateVariables.put(featureValue.feature.getName(), p);
					}
					attributeVariables.put(featureValue.feature.getName(), p);
				}
			}
		}
		else{
			for (ActiveObjectActivation objectActivation : activeObjectActivations.values()) {
				for (FeatureValue featureValue : objectActivation.object.featureValues) {
					
					if (featureValue.feature.getAppliedStereotype("testing::StateVariable") != null) {
						String name = featureValue.feature.getName();
						PropertyUtil p = stateVariables.get(name);
						p.featureValue = featureValue;
					}
					else{
						String name = featureValue.feature.getName();
						PropertyUtil p = attributeVariables.get(name);
						p.featureValue = featureValue;
					}
				}
			}
		}
		
	}
	
	public synchronized Map<NamedElement, StateActivation> getAllStateActivations() {

		Map<NamedElement, StateActivation> res = new HashMap<NamedElement, StateActivation>();
		
		for(ActiveObjectActivation activeObjectActivation : activeObjectActivations.values()){
			StateMachineExecution execution = (StateMachineExecution) (activeObjectActivation.classifierBehaviorInvocations
					.get(0).execution);
			for (RegionActivation regionActivation : execution.getRegionActivation()) {
				for (VertexActivation vertextActivation : regionActivation.getVertexActivations()) {
					if (vertextActivation instanceof StateActivation) {
						getAllStateActivations((StateActivation) vertextActivation, res);
					}
				}
			}
		}

		return res;
	}

	private void getAllStateActivations(StateActivation stateActivation,
			Map<NamedElement, StateActivation> stateActivations) {

		stateActivations.put(stateActivation.getNode(), stateActivation);

		for (RegionActivation regionActivation : stateActivation.getRegionActivation()) {
			for (VertexActivation vertextActivation : regionActivation.getVertexActivations()) {
				if (vertextActivation instanceof StateActivation) {
					getAllStateActivations((StateActivation) vertextActivation, stateActivations);
				}
			}
		}
	}

	public synchronized Map<NamedElement, TransitionActivation> getAllTransitionActivations() {

		Map<NamedElement, TransitionActivation> res = new HashMap<NamedElement, TransitionActivation>();

		for (ActiveObjectActivation activeObjectActivation : activeObjectActivations.values()) {
			StateMachineExecution execution = (StateMachineExecution) (activeObjectActivation.classifierBehaviorInvocations
					.get(0).execution);
			for (RegionActivation regionActivation : execution.getRegionActivation()) {
				getAllTransitionActivations(regionActivation, res);
			}
		}

		return res;
	}

	private void getAllTransitionActivations(RegionActivation regionActivation,
			Map<NamedElement, TransitionActivation> transitionActivations) {

		for (TransitionActivation transitionActivation : regionActivation.getTransitionActivations()) {
			transitionActivations.put(transitionActivation.getNode(), transitionActivation);
		}

		for (VertexActivation vertextActivation : regionActivation.getVertexActivations()) {
			if (vertextActivation instanceof StateActivation) {
				StateActivation stateActivation = (StateActivation) vertextActivation;
				for (RegionActivation childRegionActivation : stateActivation.getRegionActivation()) {
					getAllTransitionActivations(childRegionActivation, transitionActivations);
				}
			}
		}
	}
	
}
