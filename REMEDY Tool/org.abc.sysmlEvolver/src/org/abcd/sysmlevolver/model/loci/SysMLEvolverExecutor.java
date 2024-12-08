package org.abcd.sysmlevolver.model.loci;

import java.util.List;

import org.abcd.sysmlevolver.debug.TestControlDelegate;
import org.eclipse.papyrus.moka.composites.Semantics.CompositeStructures.StructuredClasses.CS_Object;
import org.eclipse.papyrus.moka.composites.Semantics.CompositeStructures.StructuredClasses.CS_Reference;
import org.eclipse.papyrus.moka.composites.Semantics.Loci.LociL3.CS_Executor;
import org.eclipse.papyrus.moka.fuml.FUMLExecutionEngine;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Object_;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Reference;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.uml2.uml.Class;

public class SysMLEvolverExecutor extends CS_Executor {



	@Override
	public Reference start(Class type, List<ParameterValue> inputs) {

		((TestControlDelegate) FUMLExecutionEngine.eInstance.getControlDelegate()).clear();




		Reference reference;
		if (object instanceof CS_Object) {
			reference = new CS_Reference();
			((CS_Reference) reference).compositeReferent = (CS_Object) object;
		} else {
			reference = new Reference();
		}
		reference.referent = object;

		return reference;
	}

	

}
