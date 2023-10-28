package org.abc.constraint.node.operationnode;

import org.abc.constraint.node.ConstraintNode;

public abstract class OperationNode extends ConstraintNode {

	protected ConstraintNode sourceNode;
	protected ConstraintNode argumentNode;
	
	
	// "oclIsTypeOf", "oclIsKindOf", "oclIsNew", "oclIsUndefined", "oclIsInvalid","notEmpty","isEmpty"
	public static String symbol = "DEFAULT";
	
	public OperationNode(ConstraintNode sourceNode, ConstraintNode argumentNode){
		this.sourceNode = sourceNode;
		this.argumentNode = argumentNode;
	}
	
	public double getRange(){
		
		double r1 = sourceNode.getRange();
		double r2 = argumentNode.getRange();
		
		if(r1 != 0){
			return r1;
		}
		else if(r2 != 0){
			return r2;
		}
		else{
			return 0;
		}
		
	}
	
}
