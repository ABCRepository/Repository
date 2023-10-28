package org.abcd.sysmlevolver.test.utils;

import java.util.List;

import org.abcd.sysmlevolver.test.invocation.Invocation;
import org.eclipse.uml2.uml.State;

public class FaultySenario {
	
	public List<State> substates;
	public Invocation invocation;
	
	public FaultySenario(List<State> substates, Invocation invocation) {
		this.substates = substates;
		this.invocation = invocation;
	}
	
}
