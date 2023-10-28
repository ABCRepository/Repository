package org.abc.constraint.node.operationnode;

import org.abc.constraint.node.ConstraintNode;

public class PlusOpNode extends OperationNode {
	
	public static String symbol = "+";
	
	public PlusOpNode(ConstraintNode sourceNode, ConstraintNode argumentNode) {
		super(sourceNode, argumentNode);
	}

	@Override
	public Object getValue() {
		
		Object srcValue = sourceNode.getValue();
		Object argValue = argumentNode.getValue();
		
		if(srcValue instanceof Double) {
			if(argValue instanceof Double){
				return (Double)srcValue + (Double)argValue;
			}
			else{
				return (Double)srcValue + (Integer)argValue;
			}
		}
		else{
			if(argValue instanceof Double){
				return (Integer)srcValue + (Double)argValue;
			}
			else{
				return (Integer)srcValue + (Integer)argValue;
			}
		}
	}
	

}
