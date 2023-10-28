package org.abc.constraint.node;

import java.util.ArrayList;
import java.util.List;

import org.abc.constraint.utils.IteratorVariable;

public class IteratorVariableNode extends ConstraintNode {
	
	private IteratorVariable value;
	
	private List<String> navigations = new ArrayList<String>();
	
	public IteratorVariableNode(IteratorVariable value){
		this.value = value;
	}
	
	public void addNavigation(String navigation){
		navigations.add(navigation);
	}
	
	@Override
	public Object getValue() {
		return value.getFeatureValue(navigations);
	}
	
}
