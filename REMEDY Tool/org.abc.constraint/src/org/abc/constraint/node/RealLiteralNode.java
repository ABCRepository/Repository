package org.abc.constraint.node;

public class RealLiteralNode extends ConstraintNode {

	private double value;
	
	public RealLiteralNode(double value){
		this.value = value;
	}
	
	@Override
	public Object getValue() {
		return (Double)value;
	}
	
	
	
}
