package org.abc.constraint.node;

public class StringLiteralNode extends ConstraintNode {
	private String value;
	
	public StringLiteralNode(String value){
		this.value = value;
	}
	
	@Override
	public Object getValue() {
		return value;
	}
}
