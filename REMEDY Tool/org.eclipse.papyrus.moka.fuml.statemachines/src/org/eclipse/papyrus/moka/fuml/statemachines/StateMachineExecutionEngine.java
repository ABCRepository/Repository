/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Jeremie Tatibouet (CEA LIST)
 *
 *****************************************************************************/
package org.eclipse.papyrus.moka.fuml.statemachines;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.papyrus.moka.composites.CompositeStructuresExecutionEngine;
import org.eclipse.papyrus.moka.composites.Semantics.Loci.LociL3.CS_Executor;
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.Locus;
import org.eclipse.papyrus.moka.fuml.debug.ControlDelegate;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.Loci.SM_ExecutionFactory;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.Loci.SM_Locus;
import org.eclipse.papyrus.moka.fuml.statemachines.debug.SM_ControlDelegate;
import org.eclipse.papyrus.moka.fuml.statemachines.registry.SM_SemanticStrategyRegistry;
import org.eclipse.papyrus.uml.extensionpoints.library.IRegisteredLibrary;
import org.eclipse.papyrus.uml.extensionpoints.library.RegisteredLibrary;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Type;

public class StateMachineExecutionEngine extends CompositeStructuresExecutionEngine{

	public void start(Behavior behavior) {
		if(behavior!=null){
			main = behavior;
			// creates the locus, executor and execution factory
			this.locus = new SM_Locus();
			this.locus.setFactory(new SM_ExecutionFactory());
			this.locus.setExecutor(new CS_Executor());
			// initializes built-in primitive types
			this.initializeBuiltInPrimitiveTypes(locus);
			// Initializes opaque behavior executions
			this.registerOpaqueBehaviorExecutions(locus);
			// Initializes semantic strategies
			this.registerSemanticStrategies(locus);
			// Initializes system services
			this.registerSystemServices(locus) ;
			// Initializes arguments
			this.initializeArguments(this.args) ;
			// Finally launches the execution
			this.started = true ;
			this.locus.executor.execute(main, null,this.arguments);
		}
	}
	
	@Override
	public ControlDelegate getControlDelegate() {
		if (this.controlDelegate == null)
			this.controlDelegate = new SM_ControlDelegate(this) ;
		return this.controlDelegate ;
	}
	
	protected void registerSemanticStrategies(Locus locus) {
		new SM_SemanticStrategyRegistry().registerSemanticStrategies(locus);
	}
	
	protected void initializeBuiltInPrimitiveTypes(Locus locus) {
		List<IRegisteredLibrary> libraries = RegisteredLibrary.getRegisteredLibraries();
		boolean registerBuiltInTypes = false;
		
		ResourceSet resourceSet = main.eResource().getResourceSet();
		
		for(IRegisteredLibrary l : libraries) {
			if(l.getName().equals("UMLPrimitiveTypes") ||
					l.getName().equals("fUMLTypes") || l.getName().equals("ROOMLibrary")){
				registerBuiltInTypes = true;
			}
			
			
			if(registerBuiltInTypes){
				registerBuiltInTypes = false;
				URI libraryUri = l.getUri();
				Resource libraryResource = resourceSet.getResource(libraryUri, true);
				Iterator<EObject> libElementIterator = libraryResource.getAllContents();
				if(l.getName().equals("UMLPrimitiveTypes")){
					while(libElementIterator.hasNext()){
						EObject currentElement = libElementIterator.next();
						if(currentElement instanceof PrimitiveType){
							locus.factory.addBuiltInType((Type)currentElement);
						}
					}
				}else{
					while(libElementIterator.hasNext()){
						EObject currentElement = libElementIterator.next();
						if(currentElement instanceof Classifier){
							locus.factory.addBuiltInType((Type)currentElement);
						}
					}
				}
				
			}
		}
	}
}
