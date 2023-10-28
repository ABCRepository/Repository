/*****************************************************************************
 * Copyright (c) 2014 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  CEA LIST - Initial API and implementation
 *****************************************************************************/
package org.eclipse.papyrus.moka.fuml.assertionlibrary.reporting;

import java.util.List;

import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;
import org.eclipse.uml2.uml.Classifier;

public class TestReport {

	protected Classifier context;

	protected String label;

	protected TestDecision verdict;

	protected Class<?> assertionType;
	
	protected List<Value> left;
	
	protected List<Value> right;

	public TestReport(Classifier context, String label, TestDecision verdict, Class<?> assertionType){
		this.context = context;
		this.label = label;
		this.verdict = verdict;
		this.assertionType = assertionType;
	}
	
	public TestReport(Classifier context, String label, TestDecision verdict, Class<?> assertionType, List<Value> left, List<Value> right) {
		this.context = context;
		this.label = label;
		this.verdict = verdict;
		this.assertionType = assertionType;
		this.left = left;
		this.right = right;
	}

	public String getLabel() {
		return label;
	}

	public Classifier getContext() {
		return context;
	}

	public TestDecision getVerdict() {
		return verdict;
	}

	public Class<?> getAssertionType() {
		return this.assertionType;
	}

	@Override
	public String toString() {
		String contextName = context == null ? "NULL" : (context.getQualifiedName() == null ? "NULL" : context.getQualifiedName());
		String s = "[TEST] " + this.label + " in " + contextName + " => " + verdict.toString();
		if(this.left!=null && this.right!=null && verdict == TestDecision.FAILED){
			s += " ("+this.left + " != " + this.right+")";
		}
		return s;
	}

}
