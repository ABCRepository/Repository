package org.abcd.sysmlevolver.test.invocation;

import java.util.List;

import org.abcd.sysmlevolver.model.loci.ActiveObjectActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Operation;

public class FaultInjectionItem {
	
	private ActiveObjectActivation objectActivation;
	private Operation operation;
	private List<ParameterValue> parameters;
	
	private int flag = 2;
	
	public FaultInjectionItem(ActiveObjectActivation objectActivation, Operation operation, List<ParameterValue> parameters){
		this.objectActivation = objectActivation;
		this.operation = operation;
		this.parameters = parameters;
	}
	
	public void trigger(){
		objectActivation.callOperation(operation, parameters);
	}
	
	public boolean canTrigger(){
		flag--;
		if(flag < 0){
			return true;
		}
		return false;
	}
	
}
