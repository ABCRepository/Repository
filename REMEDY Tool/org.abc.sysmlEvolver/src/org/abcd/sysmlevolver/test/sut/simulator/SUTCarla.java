package org.abcd.sysmlevolver.test.sut.simulator;

import java.util.ArrayList;
import java.util.List;

import org.abcd.sysmlevolver.test.utils.PropertyUtil;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.BooleanValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.FeatureValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.IntegerValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.RealValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;

public class SUTCarla extends SUTSimulator {


	@Override
	public void reset() {

	}


	public Object getFeatureValue(String curAttrname, String Msg) {
		String[] pairs = Msg.split(";");
		for (String pair : pairs) {
			String[] key_value = pair.split(":");
			if (key_value.length < 2) {
				continue;
			}
			if (key_value[0].trim().equals(curAttrname)) {
				
					FeatureValue featureValue = new FeatureValue();
					IntegerValue realValue = new IntegerValue();
					realValue.value = Integer.parseInt(key_value[1].trim());
					List<Value> values = new ArrayList<Value>();
					values.add(realValue);
					featureValue.values = values;
					return featureValue;
				
			}
		}
		return null;
	}

	

	
	
	
	
	

}
