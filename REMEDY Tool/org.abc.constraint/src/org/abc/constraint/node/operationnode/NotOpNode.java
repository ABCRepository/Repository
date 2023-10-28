package org.abc.constraint.node.operationnode;

import org.abc.constraint.node.ConstraintNode;

public class NotOpNode extends OperationNode {

	public static String symbol = "not";
	
	public NotOpNode(ConstraintNode sourceNode, ConstraintNode argumentNode) {
		super(sourceNode, argumentNode);
	}

	@Override
	public Object getValue() {
		return !(Boolean)sourceNode.getValue();
	}
	
	@Override
	public double getSafeDistance(){
		return 1 - sourceNode.getSafeDistance();
	}
}
