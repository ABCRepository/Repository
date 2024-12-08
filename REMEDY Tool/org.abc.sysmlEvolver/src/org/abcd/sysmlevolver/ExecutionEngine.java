package org.abcd.sysmlevolver;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.abcd.sysmlevolver.debug.TestControlDelegate;
import org.abcd.sysmlevolver.model.loci.ActiveObjectActivation;
import org.abcd.sysmlevolver.model.loci.SysMLEvolverExecutionFactory;
import org.abcd.sysmlevolver.model.loci.SysMLEvolverExecutor;
import org.abcd.sysmlevolver.model.loci.SysMLEvolverLocus;
import org.abcd.sysmlevolver.test.log.PerformanceLogger;
import org.abcd.sysmlevolver.test.log.TestLogger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrus.moka.async.fuml.FUMLAsyncExecutionEngine;
import org.eclipse.papyrus.moka.async.fuml.debug.AsyncControlDelegate;
import org.eclipse.papyrus.moka.debug.MokaDebugTarget;
import org.eclipse.papyrus.moka.fuml.debug.ControlDelegate;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Class;

public class ExecutionEngine extends FUMLAsyncExecutionEngine {

	@Override
	public void init(EObject eObjectToExecute, String[] args, MokaDebugTarget debugTarget, int requestPort,
			int replyPort, int eventPort) throws UnknownHostException, IOException {
		super.init(eObjectToExecute, args, debugTarget, requestPort, replyPort, eventPort);
		if (ExecutionParameters.logPerformance) {
			PerformanceLogger.init();
		}

	}

	@Override
	public void start(Behavior behavior) {
		if (behavior != null) {
			main = behavior;

			// initializes built-in primitive types
			this.initializeBuiltInPrimitiveTypes(locus);

			// Initializes opaque behavior executions
			this.registerOpaqueBehaviorExecutions(locus);

			// Initializes semantic strategies
			this.registerSemanticStrategies(locus);

			// Initializes system services
			this.registerSystemServices(locus);

			// Initializes arguments
			this.initializeArguments(this.args);

			
		}

	}


	
}
