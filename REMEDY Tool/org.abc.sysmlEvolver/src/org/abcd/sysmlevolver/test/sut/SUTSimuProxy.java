package org.abcd.sysmlevolver.test.sut;

import org.abcd.sysmlevolver.test.sut.simulator.SUTCarla;
import org.abcd.sysmlevolver.test.sut.simulator.SUTSimulator;

public class SUTSimuProxy extends SUTProxy {

	private SUTSimulator sutSimulator;

	public SUTSimuProxy() {
		sutSimulator = new SUTCarla();
	}

	public void init(String sutPort, String mavproxyPort) {

		 if (SUTObject.name.startsWith("Virtual Vehicle in Carla")) {
			sutSimulator = new SUTCarla();
		}

	}

	public void reset() {
		super.reset();
		sutSimulator.reset();
	}


	
	@Override
	public void setSUTObject(SUTObject sut) {
		sutSimulator.setSUTObject(sut);
	}

}
