package org.abcd.sysmlevolver.test.sut;

import java.util.HashMap;
import java.util.Map;

import org.abcd.sysmlevolver.ExecutionParameters;
import org.abcd.sysmlevolver.test.sut.SUTConnector.MsgType;

public class SUTProxy {

	private Map<String, SUTConnector> connectors = new HashMap<String, SUTConnector>();

	protected static SUTProxy instance = null;

	private String sutPort;
	private String mavproxyPort;

	protected SUTProxy() {

	}

	public void init(String sutPort, String mavproxyPort) {
		this.sutPort = sutPort;
		this.mavproxyPort = mavproxyPort;
	}

	public void reset() {
		for (SUTConnector connector : connectors.values()) {
			connector.close();
		}
		connectors.clear();
	}

	public boolean send(String host, String port, String str) {

		if (port.equals("10008")) {
			// port = sutPort;
		} else if (port.equals("20008")) {
			// port = mavproxyPort;
		} else {
			// port = mavproxyPort;
		}

		host = host.trim();
		port = port.trim();

		SUTConnector connector = null;

		connector = connectors.get(host + port); //
		if (connector == null) {
			connector = new SUTConnector(host, port);
			connectors.put(host + port, connector);

		}

		return connector.send(str);
	}

	public String receive(String host, String port, MsgType type) {
		host = host.trim();
		port = port.trim();

		if (port.equals("10008")) {
			// port = sutPort;
		} else if (port.equals("20008")) {
			// port = mavproxyPort;
		}

		SUTConnector connector = connectors.get(host + port);
		if (connector == null) {
			connector = new SUTConnector(host, port);
			connectors.put(host + port, connector);
		}

		return connector.receive(type);
	}

	public void setSUTObject(SUTObject sut) {

	}

	public static SUTProxy instance() {
		if (instance == null) {
			if (ExecutionParameters.simulating) {
				instance = new SUTSimuProxy();
			} else {
				instance = new SUTProxy();
			}
		}
		return instance;
	}

}
