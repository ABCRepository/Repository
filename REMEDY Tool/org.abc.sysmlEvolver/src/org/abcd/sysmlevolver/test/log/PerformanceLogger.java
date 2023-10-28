package org.abcd.sysmlevolver.test.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.abc.constraint.ConstraintInstance;

public class PerformanceLogger {
	public static String basePath = "./logs";

	private static PrintWriter exitStateLog;
	private static PrintWriter enterStateLog;
	private static PrintWriter exeOpLog;
	private static PrintWriter tranLog;
	private static PrintWriter genDataLog;
	private static PrintWriter evalLog;
	private static PrintWriter genUncLog;
	private static PrintWriter evolveStatesAndTransLog;

	public static void init() {
		try {
			exitStateLog = new PrintWriter(new FileOutputStream(new File(basePath + "/exit state.txt"), true));
			enterStateLog = new PrintWriter(new FileOutputStream(new File(basePath + "/enter state.txt"), true));
			exeOpLog = new PrintWriter(new FileOutputStream(new File(basePath + "/execute operation.txt"), true));
			tranLog = new PrintWriter(new FileOutputStream(new File(basePath + "/traverse transition.txt"), true));
			genDataLog = new PrintWriter(new FileOutputStream(new File(basePath + "/generate test data.txt"), true));
			evalLog = new PrintWriter(new FileOutputStream(new File(basePath + "/evaluate constraint.txt"), true));
			genUncLog = new PrintWriter(new FileOutputStream(new File(basePath + "/introduce uncertainty.txt"), true));
			evolveStatesAndTransLog = new PrintWriter(
					new FileOutputStream(new File(basePath + "/evolve states and trans.txt"), true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static synchronized void beginTestTraceExecution(int testIndex) {

		try {
			exitStateLog = new PrintWriter(new FileOutputStream(new File(basePath + "/exit state.txt"), true));
			enterStateLog = new PrintWriter(new FileOutputStream(new File(basePath + "/enter state.txt"), true));
			exeOpLog = new PrintWriter(new FileOutputStream(new File(basePath + "/execute operation.txt"), true));
			tranLog = new PrintWriter(new FileOutputStream(new File(basePath + "/traverse transition.txt"), true));
			genDataLog = new PrintWriter(new FileOutputStream(new File(basePath + "/generate test data.txt"), true));
			evalLog = new PrintWriter(new FileOutputStream(new File(basePath + "/evaluate constraint.txt"), true));
			genUncLog = new PrintWriter(new FileOutputStream(new File(basePath + "/introduce uncertainty.txt"), true));
			evolveStatesAndTransLog = new PrintWriter(
					new FileOutputStream(new File(basePath + "/evolve states and trans.txt"), true));

			exitStateLog.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\ntest trace " + testIndex + " : ");
			enterStateLog.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\ntest trace " + testIndex + " : ");
			exeOpLog.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\ntest trace " + testIndex + " : ");
			tranLog.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\ntest trace " + testIndex + " : ");
			genDataLog.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\ntest trace " + testIndex + " : ");
			evalLog.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\ntest trace " + testIndex + " : ");
			genUncLog.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\ntest trace " + testIndex + " : ");
			evolveStatesAndTransLog.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\ntest trace " + testIndex + " : ");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		exitStateLog.close();
		enterStateLog.close();
		exeOpLog.close();
		tranLog.close();
		genDataLog.close();
		evalLog.close();
		genUncLog.close();
		evolveStatesAndTransLog.close();

	}

	public static void logExitState(long timeCost) {
		exitStateLog.println(timeCost);
	}

	public static void logEnterState(long timeCost) {
		enterStateLog.println(timeCost);
	}

	public static void logEvaluateConstraint(long timeCost, ConstraintInstance constraint) {
		evalLog.println(timeCost);
	}

	public static void logExecuteOperation(long timeCost) {
		exeOpLog.println(timeCost);
	}

	public static void logGenerateTestData(long timeCost) {
		genDataLog.println(timeCost);
	}

	public static void logEvolveStatesAndTrans(long timeCost) {
		evolveStatesAndTransLog.println(timeCost);
	}

	public static void logTraverseTrans(long timeCost) {
		tranLog.println(timeCost);
	}

	public static void close() {
		exitStateLog.close();
		enterStateLog.close();
		exeOpLog.close();
		tranLog.close();
		genDataLog.close();
		evalLog.close();
		genUncLog.close();
		evolveStatesAndTransLog.close();
	}

}
