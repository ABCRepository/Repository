package org.abcd.sysmlevolver.test.utils;

import org.abcd.sysmlevolver.model.loci.ActiveObjectActivation;
import org.eclipse.uml2.uml.Operation;

public class OperationUtil {
	
	public Operation operation;
	public ActiveObjectActivation objectActivation;
	
	public OperationUtil(Operation operation, ActiveObjectActivation objectActivation) {
		this.operation = operation;
		this.objectActivation = objectActivation;
	}
	
}
