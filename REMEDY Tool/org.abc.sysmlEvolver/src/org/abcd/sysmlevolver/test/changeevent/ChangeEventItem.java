package org.abcd.sysmlevolver.test.changeevent;

import org.abc.constraint.BooleanValueSpecificationInstance;
import org.abc.constraint.ConstraintInstanceFactory;
import org.abcd.sysmlevolver.model.loci.ActiveObjectActivation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.CommonBehavior.ChangeEventOccurrence;
import org.eclipse.uml2.uml.ChangeEvent;
import org.eclipse.uml2.uml.ValueSpecification;

public class ChangeEventItem {
	
	private ActiveObjectActivation objectActivation = null;
	private ValueSpecification changeExpression = null;
	private BooleanValueSpecificationInstance specificationInstance = null;
	
	public ChangeEventItem(ActiveObjectActivation objectActivation, ValueSpecification changeExpression){
		this.objectActivation = objectActivation;
		this.changeExpression = changeExpression;
		
		specificationInstance = ConstraintInstanceFactory.instance().
				createBooleanValueSpecificationInstance(changeExpression, objectActivation.object);
	}
	
	public boolean evaluate(){
		return specificationInstance.evaluate();
	}
	
	public void sendChangeEvent(){
		ChangeEventOccurrence changeEventOccurrence = new ChangeEventOccurrence();
		changeEventOccurrence.changeExpression = this.changeExpression;
		objectActivation.send(changeEventOccurrence);
		
	}
	
	public boolean equals(ChangeEvent event){
		if(event == null || event.getChangeExpression() == null){
			System.out.println();
		}
		
		if(changeExpression.equals(event.getChangeExpression())){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean equals(ChangeEventItem item){
		
		if(objectActivation.equals(item.objectActivation) && 
			changeExpression.equals(item.changeExpression)){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public String toString() {
		return changeExpression.toString()+"--"+specificationInstance;
	}
	
	
	
}
