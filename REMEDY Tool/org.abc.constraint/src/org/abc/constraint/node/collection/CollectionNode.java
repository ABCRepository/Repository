package org.abc.constraint.node.collection;

import java.util.List;

import org.abc.constraint.node.ConstraintNode;
import org.abc.constraint.utils.IteratorVariable;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.FeatureValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Object_;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;
import org.eclipse.uml2.uml.Feature;

public abstract class CollectionNode extends ConstraintNode{
	
	protected List<Value> collection;
	
	protected IteratorVariable[] iterators = new IteratorVariable[2];
	
	public CollectionNode(List<Value> collection, IteratorVariable iterator1, IteratorVariable iterator2){
		this.collection = collection;
		iterators[0] = iterator1;
		iterators[1] = iterator2;
	}
	
}
