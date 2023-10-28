package org.abc.constraint.node;

import org.eclipse.uml2.uml.EnumerationLiteral;

public class EnumerationLiteralNode extends ConstraintNode {
	
	private EnumerationLiteral literal;
	
	public EnumerationLiteralNode(EnumerationLiteral literal){
		this.literal = literal;
	}
	
	@Override
	public Object getValue() {
		return literal;
	}
}
