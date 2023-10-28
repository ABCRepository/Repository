package org.abcd.sysmlevolver.test.sut.simulator;

import java.util.Random;

import org.abcd.sysmlevolver.test.sut.SUTObject;

public abstract class SUTSimulator {
	
	protected SUTObject sut;
	
	protected Random random = new Random();
	
	public void setSUTObject(SUTObject sut) {
		this.sut = sut;
	}
	
	public abstract void reset();
	
	
	
	public abstract Object getFeatureValue(String curAttrname, String Msg);
	

	
}
