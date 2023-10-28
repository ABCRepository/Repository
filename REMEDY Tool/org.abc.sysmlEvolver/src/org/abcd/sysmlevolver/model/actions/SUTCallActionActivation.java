package org.abcd.sysmlevolver.model.actions;

import java.util.List;

import org.abcd.sysmlevolver.test.sut.SUTProxy;
import org.eclipse.papyrus.moka.fuml.Semantics.Actions.BasicActions.ActionActivation;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;
import org.eclipse.uml2.uml.InputPin;
import org.eclipse.uml2.uml.OpaqueAction;

public class SUTCallActionActivation extends ActionActivation {

	@Override
	public void doAction() {

		OpaqueAction opaqueAction = (OpaqueAction) node;
		String script = opaqueAction.getBodies().get(0);


		int hostEnd = script.indexOf(':');
		assert (hostEnd > 0);
		String host = script.substring(0, hostEnd);
		int portEnd = script.indexOf('/', hostEnd);
		assert (portEnd > 0);
		String port = script.substring(hostEnd + 1, portEnd);
		String cmd = script.substring(portEnd + 1);

		List<InputPin> inputPins = opaqueAction.getInputs();
		for (InputPin inputPin : inputPins) {
			String name = inputPin.getName();
			List<Value> values = this.takeTokens(inputPin);
			assert (values.size() == 1);
			String value = values.get(0).specify().stringValue();

			cmd = cmd.replace("$" + name, value);
		}


		if (!SUTProxy.instance().send(host, port, cmd)) {
			this.terminate();
		}

		if (cmd.equals("stop")) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
