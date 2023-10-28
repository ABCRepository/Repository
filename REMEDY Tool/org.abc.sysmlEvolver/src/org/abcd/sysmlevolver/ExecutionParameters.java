package org.abcd.sysmlevolver;


public class ExecutionParameters {

	// initial configuration parameters
	public static int maxEpisodeNum = 30;

	public static int maxWaitingSeconds4FaultHealing = 300;

	public static int uncertaintyIntroducingFrequency = 1; // 200

	public static boolean simulating = true;// true

	public static boolean introducingUncertainties = true;

	public static boolean usingSarsa = true;// true

	public static int maxActionExplorationTimes = 200;

	public static int maxUncertaintyExplorationTimes = 2;

	public static boolean logPerformance = true;


	public static boolean isTerminate = false;

	// output and print information for analysis
	public static int numsOfEvolvedStatesofCall = 0;

	public static int numsOfEvolvedTransitionofCall = 0;

	public static int numsOfEvolvedStatesofChange = 0;

	public static int numsOfEvolvedTransitionofChange = 0;

	public static int numsOfViolatedConstraints = 0;

}
