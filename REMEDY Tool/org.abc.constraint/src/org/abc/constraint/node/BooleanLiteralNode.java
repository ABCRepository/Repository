package org.abc.constraint.node;

public class BooleanLiteralNode extends ConstraintNode{
	
	private boolean value;
	
	public BooleanLiteralNode(boolean value){
		this.value = value;
	}
	
	@Override
	public Object getValue() {
		return value;
	}
}
