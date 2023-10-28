package org.abcd.sysmlevolver.model.Semantics;

import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.FeatureValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Object_;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.StateMachineExecution;
import org.eclipse.uml2.uml.Class;

public class SysMLEvolverStateMachineExecution extends StateMachineExecution{
	
	public SysMLEvolverStateMachineExecution(){
		super();
	}
	
	@Override
	public void execute() {
		
		for(FeatureValue feature : context.featureValues){
			if(feature.feature instanceof Class){
				for(Value object : feature.values){
					((Object_)object).startBehavior((Class)feature.feature, null);
				}
			}
		}
		
		super.execute();
		
	}
	
}
