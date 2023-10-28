package org.abcd.sysmlevolver.test.reinforcement.qlearning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class Interval{
	public double lower;
	public double upper;
	
	public Interval(double min, double max){
		this.lower = min;
		this.upper = max;
	}
}

public class QIntervalValue {
	
	private static Random random = new Random();
	
	private int zeroNum = 0;
	
	private List<Double> samples = null;
	private double sum = 0;
	
	private Interval confidenceInterval = new Interval(0, 0);
	
	private boolean changed = false;
	
	public void addSample(double sample){
		
		if(sample == 0){
			if(samples != null){
				System.err.println("sample zero!!");
			}
			else{
				zeroNum ++;
				return;
			}
		}
		else{
			if(zeroNum > 0){
				System.err.println("sample not zero!!");
				zeroNum ++;
				return;
			}
			else if(samples == null){
				samples = new ArrayList<Double>();
			}
		}
		
		boolean added = false;
		for(int i = 0; i < samples.size(); i++){
			double value = samples.get(i);
			if(sample > value){
				samples.add(i, sample);
				added = true;
				break;
			}
		}
		
		if(!added){
			samples.add(sample);
		}
		
		sum += sample;
		changed = true;
	}
	
	public int getSampleSize(){
		if(samples == null){
			return zeroNum;
		}
		else{
			return samples.size();
		}
	}
	
	public double getMax(){
		if(samples == null){
			return 0;
		}
		
		double max = 0;
		for(int i = 0; i < samples.size(); i++){
			if(max < samples.get(i)){
				max = samples.get(i);
			}
		}
		return max;
	}
	
	public double getUpperValue(){
		if(changed){
			bootstrapping();
			changed = false;
		}
		
		return confidenceInterval.upper;
	}
	
	public double getLowerValue(){
		if(changed){
			bootstrapping();
			changed = false;
		}
		
		return confidenceInterval.lower;
	}
	
	public double getMean(){
		if(samples == null){
			return 0;
		}
		
		return sum/samples.size();
	}
	
	private void bootstrapping(){
		
		double mean = sum / samples.size();
		
		double variances[] = new double[1000];
		
		for(int i = 0; i < variances.length; i ++){
			double bootstrappingMean = getBootstrappingMean();
			double bootstrappingVariance = bootstrappingMean - mean;
			variances[i] = bootstrappingVariance;
		}
		
		Arrays.sort(variances);
		
		confidenceInterval.lower = mean - variances[990];
		confidenceInterval.upper = mean - variances[0];
	}
	
	private double getBootstrappingMean(){
		double sum = 0;
		for(int i = 0; i < 10; i++){
			double sample = samples.get(random.nextInt(samples.size() / 2 + 1));
			sum += sample;
		}
		return sum / 10;
	}
	
	public String toString(){
		
		StringBuffer str = new StringBuffer();
		str.append("(");
		
		if(samples == null){
			str.append("zero sample n:");
			str.append(zeroNum);
		}
		else{
			for(Double sample : samples){
				str.append(sample);
				str.append(", ");
			}
		}
		
		str.append(")");
		
		return str.toString();
	}
	
	
}
