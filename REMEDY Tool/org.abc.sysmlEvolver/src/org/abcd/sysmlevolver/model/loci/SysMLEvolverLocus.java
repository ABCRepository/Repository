package org.abcd.sysmlevolver.model.loci;

import java.util.ArrayList;
import java.util.List;

import org.abcd.sysmlevolver.model.Semantics.ActiveObject;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Object_;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.Loci.SM_Locus;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Class;

public class SysMLEvolverLocus extends SM_Locus{
	
	
	@Override
	public Object_ instantiate(Class type) {
		Object_ object = null;
		if (type instanceof Behavior) {
			object = this.factory.createExecution((Behavior) type, null);
		} else {
			object = new ActiveObject();
			object.locus = this;
			object.types.add(type);
			object.createFeatureValues();
			
			this.add(object);
		}
		return object;
	}
	
	
	
}
