package org.abc.constraint.node.collection;

import java.util.ArrayList;
import java.util.List;

import org.abc.constraint.node.ConstraintNode;
import org.abc.constraint.utils.IteratorVariable;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Object_;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;

public class ForAllNode extends CollectionNode {
	
	ConstraintNode expNode;
	
	public ForAllNode(List<Value> collection, IteratorVariable iterator1, IteratorVariable iterator2,
			ConstraintNode expNode) {
		super(collection, iterator1, iterator2);
		this.expNode = expNode;
	}

	@Override
	public Object getValue() {
		
		if(iterators[1] == null){
			return getValueOneIterator();
		}
		else{
			return getValueTwoIterator();
		}
	}
	
	private Object getValueOneIterator(){
		for(Value value : collection){
			iterators[0].setCurObject((Object_)value);
			if(!((Boolean)expNode.getValue())){
				return false;
			}
		}
		return true;
	}
	
	private Object getValueTwoIterator(){
		for(int i = 0; i < collection.size(); i ++){
			iterators[0].setCurObject((Object_)collection.get(i));
			for(int j = 0; j < collection.size(); j ++){
				iterators[1].setCurObject((Object_)collection.get(j));
				if(!((Boolean)expNode.getValue())){
					return false;
				}
			}
		}
		return true;
	}
	
}
