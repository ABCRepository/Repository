package org.abc.constraint.node.operationnode;

import org.abc.constraint.node.ConstraintNode;

public class OrOpNode extends OperationNode {
	
	public static String symbol = "or";
	
	public OrOpNode(ConstraintNode sourceNode, ConstraintNode argumentNode) {
		super(sourceNode, argumentNode);
	}

	@Override
	public Object getValue() {
		return (Boolean)sourceNode.getValue() || (Boolean)argumentNode.getValue();
	}
	
	@Override
	public double getSafeDistance(){
		double d1 = sourceNode.getSafeDistance();
		double d2 = argumentNode.getSafeDistance();
		return (d1 < d2) ? d2 : d1;
	}
	
	@Override
	public double getViolationDistance(){
		double d1 = sourceNode.getViolationDistance();
		double d2 = argumentNode.getViolationDistance();
		return (d1 < d2) ? d1 : d2;
	}
}
