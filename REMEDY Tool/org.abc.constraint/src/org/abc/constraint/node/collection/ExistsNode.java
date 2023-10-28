package org.abc.constraint.node.collection;

import java.util.List;

import org.abc.constraint.node.ConstraintNode;
import org.abc.constraint.utils.IteratorVariable;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Object_;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;

public class ExistsNode extends CollectionNode{
	
ConstraintNode expNode;
	
	public ExistsNode(List<Value> collection, IteratorVariable iterator1, IteratorVariable iterator2,
			ConstraintNode expNode) {
		super(collection, iterator1, null);
		this.expNode = expNode;
		
		assert (iterator1 != null);
		assert (iterator2 == null);
	}

	@Override
	public Object getValue() {
		
		for(Value value : collection){
			iterators[0].setCurObject((Object_)value);
			if(((Boolean)expNode.getValue())){
				return true;
			}
		}
		return false;
	}
	
}
