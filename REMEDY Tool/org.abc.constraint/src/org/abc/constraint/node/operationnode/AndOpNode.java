package org.abc.constraint.node.operationnode;

import org.abc.constraint.node.ConstraintNode;

public class AndOpNode extends OperationNode {
	
	public static String symbol = "and";
	
	public AndOpNode(ConstraintNode sourceNode, ConstraintNode argumentNode) {
		super(sourceNode, argumentNode);
	}

	@Override
	public Object getValue() {
		return (Boolean)sourceNode.getValue() && (Boolean)argumentNode.getValue();
	}
	
	@Override
	public double getSafeDistance(){
		double d1 = sourceNode.getSafeDistance();
		double d2 = argumentNode.getSafeDistance();
		return (d1 < d2) ? d1 : d2;
	}
	
	@Override
	public double getViolationDistance(){
		double d1 = sourceNode.getViolationDistance();
		double d2 = argumentNode.getViolationDistance();
		
		if(d1 == 0 && d2 == 0){
			return 0;
		}
		else{
			return 1 - 1 / (d1 + d2 + 1);
		}
	}
	
}
