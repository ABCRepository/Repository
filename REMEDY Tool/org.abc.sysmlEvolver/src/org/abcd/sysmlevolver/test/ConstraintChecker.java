package org.abcd.sysmlevolver.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.abc.constraint.ConstraintInstance;
import org.abcd.sysmlevolver.ExecutionParameters;
import org.abcd.sysmlevolver.debug.TestControlDelegate;
import org.abcd.sysmlevolver.model.loci.SysMLEvolverExecutor;
import org.abcd.sysmlevolver.test.log.PerformanceLogger;
import org.abcd.sysmlevolver.test.log.TestLogger;
import org.abcd.sysmlevolver.test.sut.SUTProxy;
import org.abcd.sysmlevolver.test.sut.SUTSimuProxy;
import org.eclipse.papyrus.moka.fuml.FUMLExecutionEngine;

public class ConstraintChecker {

	public ConstraintInstance violatedConstraint = null;



	private CopyOnWriteArraySet<ConstraintInstance> constraints;

	private SysMLEvolverExecutor executor;

	public ConstraintChecker(SysMLEvolverExecutor executor) {
		this.executor = executor;

		constraints = new CopyOnWriteArraySet<ConstraintInstance>();
	}

	public void init() {
		constraints.clear();

		violatedConstraint = null;
	}

	public synchronized void addConstraint(ConstraintInstance constraint) {
		constraints.add(constraint);
		System.out.println("add constraint :" + constraint);
	}

	public synchronized void removeConstraint(ConstraintInstance constraint) {
		constraints.remove(constraint);
		// java.util.ConcurrentModificationException
		System.out.println("remove constraint :" + constraint);
	}


	public void logCheckInvariantsTimeCost() {
		for (ConstraintInstance constraint : constraints) {
			long beginTime = System.nanoTime();

			constraint.evaluate();

			long stopTime = System.nanoTime();
			if (ExecutionParameters.logPerformance) {
				PerformanceLogger.logEvaluateConstraint(stopTime - beginTime, constraint);
			}
		}
	}

	/**
	 * 
	 * @param newTriggerGenerated
	 * @return distance to fail
	 */
	public synchronized double checkInvariants(boolean newTriggerGenerated) {

		

		System.out.println("~~~ current constraint ~~~:" + constraints);
		for (ConstraintInstance constraint : constraints) {
			if (FUMLExecutionEngine.eInstance.isTerminated()) {
				return -1;
			}

			if (newTriggerGenerated) {
				constraint.violatedTimes = 0;
			}

			if (constraint.isSafeCondition()) {// !constraint.isSafeCondition()
				double d = constraint.getSafeDistance();
				

				if (d == 0) {
					System.err.println("crucial error detected: " + constraint);
					((TestControlDelegate) FUMLExecutionEngine.eInstance.getControlDelegate())
							.notifySafeConditionViolated(constraint);
					return 0;
				
				}
			} else {
				if (!constraint.evaluate()) {
					constraint.violatedTimes++;
					if (constraint.violatedTimes > 0) {
						violatedConstraint = constraint;
						ExecutionParameters.numsOfViolatedConstraints++;
						
						return -1;
					}
				} else {
					
					constraint.violatedTimes = 0;
				}
			}

			

		}
		return 0;
	}

	

}
