package org.abc.constraint.node;

public class IntegerLiteralNode extends ConstraintNode {
	
	private int value;
	
	public IntegerLiteralNode(int value){
		this.value = value;
	}
	
	@Override
	public Object getValue() {
		return (Integer)value;
	}

}
