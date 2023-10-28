package org.abc.constraint.node.operationnode;

import org.abc.constraint.node.ConstraintNode;

public class MinusOpNode extends OperationNode {

	public static String symbol = "-";
	
	public MinusOpNode(ConstraintNode sourceNode, ConstraintNode argumentNode) {
		super(sourceNode, argumentNode);
	}

	@Override
	public Object getValue() {
		
		if(argumentNode == null){
			Object value = sourceNode.getValue();
			if(value instanceof Double){
				return -(Double)value;
			}
			else if(value instanceof Integer){
				return -(Integer)value;
			}
			else{
				return null;
			}
		}
		else{
			Object srcValue = sourceNode.getValue();
			Object argValue = argumentNode.getValue();
			
			if(srcValue instanceof Double) {
				if(argValue instanceof Double){
					return (Double)srcValue - (Double)argValue;
				}
				else{
					return (Double)srcValue - (Integer)argValue;
				}
			}
			else{
				if(argValue instanceof Double){
					return (Integer)srcValue - (Double)argValue;
				}
				else{
					return (Integer)srcValue - (Integer)argValue;
				}
			}
		}
		
		
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
