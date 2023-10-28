package org.abc.constraint.node.operationnode;

import org.abc.constraint.node.ConstraintNode;

public class AbsOpNode extends OperationNode {
	
public static String symbol = "abs";
	
	public AbsOpNode(ConstraintNode sourceNode, ConstraintNode argumentNode) {
		super(sourceNode, argumentNode);
	}

	@Override
	public Object getValue() {
		Object sourceValue = argumentNode.getValue();
		if(sourceValue instanceof Integer ){
			return Math.abs((Integer)sourceValue);
		}
		else if(sourceValue instanceof Double ){
			return Math.abs((Double)sourceValue);
		}
		return null;
	}
	
	public double getRange(){
		return argumentNode.getRange();
	}
	
}
