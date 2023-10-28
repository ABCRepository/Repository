package org.abc.constraint.node.operationnode;

import org.abc.constraint.node.ConstraintNode;

public class MultiplyOpNode extends OperationNode {
	
	public static String symbol = "*";
	
	public MultiplyOpNode(ConstraintNode sourceNode, ConstraintNode argumentNode) {
		super(sourceNode, argumentNode);
	}

	@Override
	public Object getValue() {
		
		Object srcValue = sourceNode.getValue();
		Object argValue = argumentNode.getValue();
		
		if(srcValue instanceof Double) {
			if(argValue instanceof Double){
				return (Double)srcValue * (Double)argValue;
			}
			else{
				return (Double)srcValue * (Integer)argValue;
			}
		}
		else{
			if(argValue instanceof Double){
				return (Integer)srcValue * (Double)argValue;
			}
			else{
				return (Integer)srcValue * (Integer)argValue;
			}
		}
	}
	
//	public double getRange(){
//		
//		double r1 = sourceNode.getRange();
//		double r2 = argumentNode.getRange();
//		
//		if(r1 != 0){
//			return r1 * getValue(argumentNode.getValue());
//		}
//		else if(r2 != 0){
//			return getValue(sourceNode.getValue()) * r2;
//		}
//		else{
//			return 0;
//		}
//		
//	}
	
}
