package org.abcd.sysmlevolver.test;

import java.io.IOException;
import java.util.List;

import org.abcd.sysmlevolver.ExecutionParameters;
import org.abcd.sysmlevolver.debug.TestControlDelegate;
import org.abcd.sysmlevolver.evolution.updater.DiagramUpdater;
import org.abcd.sysmlevolver.model.Semantics.ActiveObject;
import org.abcd.sysmlevolver.test.log.TestLogger;
import org.abcd.sysmlevolver.test.sut.SUTObject;
import org.abcd.sysmlevolver.test.sut.SUTProxy;
import org.abcd.sysmlevolver.test.sut.SUTSimuProxy;
import org.abcd.sysmlevolver.test.sut.SUTConnector.MsgType;
import org.eclipse.papyrus.moka.fuml.FUMLExecutionEngine;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.ExtensionalValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.FeatureValue;

public class TestOrchestra {

	private TestDriver driver;
	private ConstraintChecker checker;
	private FeatureValueMonitor monitor;
	private DiagramUpdater updater;

	public TestOrchestra(TestDriver driver, FeatureValueMonitor monitor, ConstraintChecker checker,
			 DiagramUpdater updater) {
		this.driver = driver;
		this.monitor = monitor;
		this.checker = checker;
		this.updater = updater;
	}

	

	private double checkInvariants(boolean newTriggerGenerated) {

		if (ExecutionParameters.logPerformance) {
			checker.logCheckInvariantsTimeCost();
		}
		return checker.checkInvariants(newTriggerGenerated);

	}

	public void run(List<ExtensionalValue> extensionalValues) throws IOException, ClassNotFoundException {

		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		double risk = 0;
		double highestRisk = 0;
		int isCollision = 0; // 1--collision, 0--no
		int isObstacleFlag = 0; // 1--obstacle, 0--no
		long beginTime = System.nanoTime();
		int index = 0;

		while (!FUMLExecutionEngine.eInstance.isTerminated() && !driver.areAllObjectsTerminated()) {
			index++;
			long stepStartTime = System.nanoTime();
			driver.waitUntilAllObjectsStable();

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			double distance = checkInvariants(newTriggerGenerated);
		
			

			if (ExecutionParameters.simulating && (isCollision == 1 || risk == 1)) {// ||
																					// isObstacleFlag
																					// ==
																					// 1
				highestRisk = 1;
				break;
			}
			if (ExecutionParameters.simulating 
					&& (isCollision == 1 || risk == 1 || isObstacleFlag == 1)) {// &&
				highestRisk = 1;
				break;
			}
			Thread.yield();
			long stepStopTime = System.nanoTime();
			System.out.println("step time---second-" + ((stepStopTime - stepStartTime) / 1000000) + (index));
			// overtime ms
			if (((stepStopTime - beginTime) / 1000000) > 100000) {
				FUMLExecutionEngine.eInstance.setIsTerminated(true);
				TestLogger.log("--------\n " + "-------**overtime**------");
			}

		}

		long stopTime = System.nanoTime();
		long durationInMS = (stopTime - beginTime) / 1000000;


		
		TestLogger.log(driver.toString());

	}

}
