package org.abcd.sysmlevolver.test.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Map;

import org.abc.constraint.ConstraintInstance;
import org.abcd.sysmlevolver.ExecutionParameters;
import org.abcd.sysmlevolver.test.invocation.Invocation;
import org.eclipse.papyrus.moka.fuml.statemachines.Semantics.StateMachines.VertexActivation;

public class TestLogger {
	public static String basePath = "./logs";

	public static String invFile = "/bestInvocations.txt";

	public static String disFile = "/distances.txt";
	public static String logFile = "/sysmlEvolverTestLog.txt";

	private static String logPath = null;

	private static PrintWriter writer;

	private static int testCaseIndex = 0;

	public static int logIndex = 0;

	public static synchronized void init() {
		logIndex = 0;
		testCaseIndex = 0;
	}

	public static synchronized void initLogParameters() {
		ExecutionParameters.numsOfViolatedConstraints = 0;
		ExecutionParameters.numsOfEvolvedStatesofCall = 0;
		ExecutionParameters.numsOfEvolvedStatesofChange = 0;

		ExecutionParameters.numsOfEvolvedTransitionofChange = 0;
		ExecutionParameters.numsOfEvolvedTransitionofCall = 0;

	}

	private static String getCurPath() {
		return basePath;
	}

	public static synchronized void beginNextTestTrace() {
		logIndex = 0;
		testCaseIndex++;

		if (logPath == null) {
			logPath = getCurPath();
		}

		String filePath = logPath + logFile;
		// System.err.println("file path---" + filePath);

		try {
			writer = new PrintWriter(new FileOutputStream(new File(filePath), true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		writer.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\ntest trace " + testCaseIndex + " : ");

		writer.close();

	}

	public static synchronized void beginTestTraceExecution(int testIndex) {

		if (logPath == null) {
			logPath = getCurPath();
		}

		String filePath = logPath + logFile;
		// System.err.println("file path---" + filePath);

		try {
			writer = new PrintWriter(new FileOutputStream(new File(filePath), true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		writer.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\ntest trace " + testIndex + " : ");

		writer.close();

	}

	public static void log(VertexActivation vertexActivation, boolean isLeaving) {

		StringBuffer str = new StringBuffer();

		str.append("\t\t");
		str.append(vertexActivation.getExecutionContext().types.get(0).getName());
		str.append(".");
		str.append(vertexActivation.getNode().getName());
		if (isLeaving)
			str.append("\t\tleave");
		else
			str.append("\t\tenter");

		log(str.toString());

	}

	public synchronized static void logParameters(String str) {
		if (logPath == null) {
			logPath = getCurPath();
		}

		String filePath = logPath + disFile;
		try {
			writer = new PrintWriter(new FileOutputStream(new File(filePath), true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		writer.println(str);
		writer.close();

		filePath = logPath + invFile;
		try {
			writer = new PrintWriter(new FileOutputStream(new File(filePath), true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		writer.println(str);
		writer.close();

	}

	public synchronized static void logBestInvocations(String str) {
		if (logPath == null) {
			logPath = getCurPath();
		}

		String filePath = logPath + invFile;
		try {
			writer = new PrintWriter(new FileOutputStream(new File(filePath), true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		writer.println(str);
		writer.close();
	}

	public synchronized static void logTestResult(Map<ConstraintInstance, Double> minimumDistances,
			double minimumSafeDistance, long timeInMS, int evolvedStatesOnCall, int evolvedStatesOnChange,
			int evolvedTransOnCall, int evolvedTransOnChange) {
		if (logPath == null) {
			logPath = getCurPath();
		}

		String filePath = logPath + disFile;
		try {
			writer = new PrintWriter(new FileOutputStream(new File(filePath), true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		writer.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\ntest trace " + testCaseIndex + " : ");
		for (ConstraintInstance constraint : minimumDistances.keySet()) {
			writer.println(constraint + ": " + minimumDistances.get(constraint));
		}

		writer.println("\n test trace length: " + logIndex);


		writer.println("\n minimum safe distance: " + minimumSafeDistance);

		writer.println("\n learning time (ms): " + timeInMS);

		writer.println("\n evolvedStatesOnCall: " + evolvedStatesOnCall);

		writer.println("\n evolvedStatesOnChange: " + evolvedStatesOnChange);

		writer.println("\n evolvedTransOnCall: " + evolvedTransOnCall);

		writer.println("\n evolvedTransOnChange: " + evolvedTransOnChange);

		writer.println("\n execution time (s): " + logIndex * 18);

		if (testCaseIndex % 10 == 0) {
			MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
			MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
			long usedMemorySize = memoryUsage.getUsed();

			writer.println("\n used memory (k): " + usedMemorySize / 1000.0);
		}

		writer.close();
	}

	public static void logError(String str) {

		log("!!! Error happens: " + str);

	}

	public static void log(String str) {
		if (logPath == null) {
			logPath = getCurPath();
		}

		String filePath = logPath + logFile;
		try {
			writer = new PrintWriter(new FileOutputStream(new File(filePath), true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		writer.println(str);
		writer.close();

	}


	public static void logExecutionData(String str) {
		if (logPath == null) {
			logPath = getCurPath();
		}
		String filePath = logPath ;
		try {
			writer = new PrintWriter(new FileOutputStream(new File(filePath), true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		writer.println(str);
		writer.close();
	}


	public static void log(Invocation invocation) {

		StringBuffer str = new StringBuffer();

		str.append(logIndex);
		str.append(". invoke ");
		str.append(invocation.toString());

		log(str.toString());
		logIndex++;
	}

}
